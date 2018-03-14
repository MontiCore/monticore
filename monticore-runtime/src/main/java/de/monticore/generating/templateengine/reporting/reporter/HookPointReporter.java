/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.Collection;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.se_rwth.commons.Names;

/**
 */
public class HookPointReporter extends AReporter {
  
  final static String SET_HOOK_POINT = "set";
  
  final static String SET_REPLACE_TEMPLATE = "setr";
  
  final static String SET_BEFORE_TEMPLATE = "setb";
  
  final static String SET_AFTER_TEMPLATE = "seta";
  
  final static String CALL_HOOK_POINT = "call";
  
  final static String CALL_REPLACE_TEMPLATE = "callr";
  
  final static String CALL_BEFORE_TEMPLATE = "callb";
  
  final static String CALL_AFTER_TEMPLATE = "calla";
  
  final static String SIMPLE_FILE_NAME = "05_HookPoint";
  
  private ReportingRepository repository;
  
  public HookPointReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportASTSpecificTemplateReplacement(java.lang.String,
   * de.monticore.ast.ASTNode, mc.codegen.HookPoint)
   */
  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate,
      ASTNode ast, HookPoint hp) {
    String astName = repository.getASTNodeNameFormatted(ast);
    String hpValue = getHookPointValue(hp);
    String simpleTemplate = ReportingHelper.getTemplateName(oldTemplate);
    String firstline = SET_HOOK_POINT
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - SET_HOOK_POINT.length());
    if (hp instanceof TemplateHookPoint) {
      firstline += "ATHP ";
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "ACHP ";
    }
    else if (hp instanceof StringHookPoint) {
      firstline += "ASHP ";
    }
    String secondLine = "";
    firstline += simpleTemplate
        + Layouter.getSpaceString(ReportingConstants.COLUMN - simpleTemplate.length())
        + astName;
    secondLine = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
    
    Reporting.reportToDetailed(firstline);
    writeLine(firstline);
    Reporting.reportToDetailed(secondLine);
    writeLine(secondLine);
    
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetHookPoint(java.lang.String,
   * mc.codegen.HookPoint)
   */
  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    reportSetHookPointHelper(hookName, hp, SET_HOOK_POINT);
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * mc.codegen.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp,
      ASTNode ast) {
    String astName = repository.getASTNodeNameFormatted(ast);
    String hpValue = getHookPointValue(hp);
    String firstline = CALL_HOOK_POINT
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - CALL_HOOK_POINT.length());
    
    boolean isThp = false;
    boolean isShp = false;
    if (hp instanceof TemplateHookPoint) {
      firstline += "THP  ";
      isThp = true;
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "CHP  ";
    }
    else if (hp instanceof StringHookPoint) {
      firstline += "SHP  ";
      isShp = true;
    }
    else {
      firstline += "EHP  ";
    }
    firstline += ReportingHelper.getHookPointName(hookName);
    // hookpoint is null
    if (hp == null) {
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
    }
    else {
      // specific handling of TemplateHookPoint:
      // THP contains an additional ASTNode
      if (isThp) {
        // template hookpoint -> astName in second line
        firstline += Layouter.getSpaceString(ReportingConstants.COLUMN - ReportingHelper.getHookPointName(hookName)
            .length()) + astName;
        Reporting.reportToDetailed(firstline);
        writeLine(firstline);
        String secondLineX = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
        Reporting.reportToDetailed(secondLineX);
        writeLine(secondLineX);
      }
      else if (isShp) {
        Reporting.reportToDetailed(firstline);
        writeLine(firstline);
        String secondLineX = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
        Reporting.reportToDetailed(secondLineX);
        writeLine(secondLineX);
      }
      else { // chp
        firstline += Layouter.getSpaceString(ReportingConstants.COLUMN - ReportingHelper.getHookPointName(hookName)
            .length()) + hpValue;
        Reporting.reportToDetailed(firstline);
        writeLine(firstline);
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    for (HookPoint hp : afterHPs) {
      reportCallHookPointHelper(oldTemplate, hp, ast, CALL_AFTER_TEMPLATE);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    for (HookPoint hp : beforeHPs) {
      reportCallHookPointHelper(oldTemplate, hp, ast, CALL_BEFORE_TEMPLATE);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      reportCallHookPointHelper(oldTemplate, hp, ast, CALL_REPLACE_TEMPLATE);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    for (HookPoint hp : hps) {
      reportCallSpecificHookPointHelper(oldTemplate, hp, ast);
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportCallHookPointEnd(java.lang.String)
   */
  @Override
  public void reportCallHookPointEnd(String hookName) {
    /* Uncomment this code line for increasing the log level */
    // writeLine(CALL_HOOK_POINT_END + ": " + hookName);
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTemplateReplacement(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportTemplateReplacement(String oldTemplate,
      List<? extends HookPoint> newHps) {
    String simpleTemplate = ReportingHelper.getTemplateName(oldTemplate);
    for (HookPoint hp : newHps) {
      reportSetTemplateHookpoint(simpleTemplate, hp, SET_REPLACE_TEMPLATE);
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetBeforeTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetBeforeTemplate(String template,
      List<? extends HookPoint> beforeHps) {
    String simpleTemplate = ReportingHelper.getTemplateName(template);
    for (HookPoint hp : beforeHps) {
      reportSetTemplateHookpoint(simpleTemplate, hp, SET_BEFORE_TEMPLATE);
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetAfterTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetAfterTemplate(String template,
      List<? extends HookPoint> afterHps) {
    String simpleTemplate = ReportingHelper.getTemplateName(template);
    for (HookPoint hp : afterHps) {
      reportSetTemplateHookpoint(simpleTemplate, hp, SET_AFTER_TEMPLATE);
    }
  }
  
  private void reportSetHookPointHelper(String hookName, HookPoint hp, String shortcut) {
    String hpValue = getHookPointValue(hp);
    
    // calculate first line
    String firstline = shortcut
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - shortcut.length());
    boolean isShp = false;
    if (hp instanceof TemplateHookPoint) {
      firstline += "THP   ";
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "CHP   ";
    }
    else if (hp instanceof StringHookPoint) {
      // string value of StringHookPoint is in an extra line
      firstline += "SHP   ";
      isShp = true;
    }
    firstline += ReportingHelper.getHookPointName(hookName);
    if (!isShp) {
      firstline += Layouter.getSpaceString(ReportingConstants.COLUMN - ReportingHelper.getHookPointName(hookName)
          .length()) + hpValue;
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
    }
    else {
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
      String secondLineX = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
      Reporting.reportToDetailed(secondLineX);
      writeLine(secondLineX);
    }
  }
  
  private void reportSetTemplateHookpoint(String simpleTemplate, HookPoint hp, String shortcut) {
    // calculate first line
    String firstline = shortcut
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - shortcut.length());
       
    boolean shp = false;
    if (hp instanceof TemplateHookPoint) {
      firstline += "THP  ";
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "CHP  ";
    }
    else if (hp instanceof StringHookPoint) {
      firstline += "SHP  ";
      shp = true;
    }
    firstline += simpleTemplate + Layouter.getSpaceString(ReportingConstants.COLUMN - simpleTemplate.length());
    String hpValue = getHookPointValue(hp);
    if (!shp) {
      firstline += hpValue;
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
    }
    else {
      // calculate and format second line
      // contains StringValue
      String secondline = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
      
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
      Reporting.reportToDetailed(secondline);
      writeLine(secondline);
    }
  }
  
  private void reportCallHookPointHelper(String oldTemplate, HookPoint hp, ASTNode ast,
      String shortcut) {
    String astName = repository.getASTNodeNameFormatted(ast);
    String hpValue = getHookPointValue(hp);
    String simpleTemplate = ReportingHelper.getTemplateName(oldTemplate);
    String firstline = shortcut
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - shortcut.length());
    boolean isShp = false;
    boolean isThp = false;
    if (hp instanceof TemplateHookPoint) {
      firstline += "THP  ";
      isThp = true;
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "CHP  ";
    }
    else if (hp instanceof StringHookPoint) {
      firstline += "SHP  ";
      isShp = true;
    }
    String secondLine = "";
    if (isShp) {
      firstline += simpleTemplate;
      secondLine = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
      Reporting.reportToDetailed(secondLine);
      writeLine(secondLine);
    }
    else if (isThp) {
      firstline += simpleTemplate
          + Layouter.getSpaceString(ReportingConstants.COLUMN - simpleTemplate.length())
          + astName;
      secondLine = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
      Reporting.reportToDetailed(secondLine);
      writeLine(secondLine);
    }
    else {
      firstline += simpleTemplate
          + Layouter.getSpaceString(ReportingConstants.COLUMN - simpleTemplate.length())
          + hpValue;
      Reporting.reportToDetailed(firstline);
      writeLine(firstline);
    }
    
  }
  
  private void reportCallSpecificHookPointHelper(String oldTemplate, HookPoint hp, ASTNode ast) {
    String astName = repository.getASTNodeNameFormatted(ast);
    String hpValue = getHookPointValue(hp);
    String simpleTemplate = ReportingHelper.getTemplateName(oldTemplate);
    String firstline = CALL_HOOK_POINT
        + Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_1 - CALL_HOOK_POINT.length());
    if (hp instanceof TemplateHookPoint) {
      firstline += "ATHP ";
    }
    else if (hp instanceof CodeHookPoint) {
      firstline += "ACHP ";
    }
    else if (hp instanceof StringHookPoint) {
      firstline += "ASHP ";
    }
    String secondLine = "";
    
    firstline += simpleTemplate
        + Layouter.getSpaceString(ReportingConstants.COLUMN - simpleTemplate.length())
        + astName;
    secondLine = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + hpValue;
    Reporting.reportToDetailed(firstline);
    writeLine(firstline);
    Reporting.reportToDetailed(secondLine);
    writeLine(secondLine);
  }
  
  private String getHookPointValue(HookPoint hp) {
    String value = null;
    if (hp != null && hp instanceof TemplateHookPoint) {
      value = ((TemplateHookPoint) hp).getTemplateName();
      value = ReportingHelper.getTemplateName(value);
    }
    else if (hp != null && hp instanceof StringHookPoint) {
      value = ((StringHookPoint) hp).getValue();
      value = ReportingHelper.formatStringToReportingString(value,
          ReportingConstants.REPORTING_ROW_LENGTH - ReportingConstants.FORMAT_LENGTH_2);
    }
    else if (hp != null && hp instanceof CodeHookPoint) {
      value = ((CodeHookPoint) hp).getClass().getName();
      value = Names.getSimpleName(value);
    }
    return value;
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    super.flush(ast);
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("The events are the following:");
    writeLine("  - set         hook point assignment");
    writeLine("  - setr        template replacement by hook points");
    writeLine("  - setb        assignment of hook points that are called before the template ");
    writeLine("  - seta        assignment of hook points that are called after the template ");
    writeLine("  - call        hook point execution");
    writeLine("  - callr       execution of hook points that replaced a template");
    writeLine("  - callb       execution of hook points that are called before the template");
    writeLine("  - calla       execution of hook points that are called after the template");
    writeLine("Hookpoints: ");
    writeLine("  - SHP         StringHookPoint");
    writeLine("  - THP         TemplateHookPoint");
    writeLine("  - CHP         CodeHookPoint");
    writeLine("  - EHP         EmptyHookPoint = HookPoint is null");
    writeLine("  - ASHP        AST StringHookPoint");
    writeLine("  - ATHP        AST TemplateHookPoint");
    writeLine("  - ACHP        AST CodeHookPoint");
    writeLine("(EOF)");
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Hookpoints");
    writeLine("Op"
        + Layouter.getSpaceString(4) + "Type"
        + Layouter.getSpaceString(1) + "HP-/AHP Info");
  }
}
