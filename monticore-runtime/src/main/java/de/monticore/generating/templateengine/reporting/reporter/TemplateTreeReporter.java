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
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.se_rwth.commons.Names;

/**
 */
public class TemplateTreeReporter extends AReporter {
  
  final static String TEMPLATE_CALL_START = "+--";
  
  final static String STRING_HOOKPOINT = "+--SHP";
  
  final static String CODE_HOOKPOINT = "+--CHP";
  
  final static String TEMPLATE_HOOKPOINT = "+--THP";
  
  final static String SPECIFIC_TEMPLATE_HOOKPOINT = "+--ATHP";
  
  final static String SPECIFIC_STRING_HOOKPOINT = "+--ASHP";
  
  final static String SPECIFIC_CODE_HOOKPOINT = "+--ACHP";
  
  final static String INSTANTIATE_JAVA_CLASS = "+--inst";
  
  final static String INDENTATION = "|  ";
  
  final static String SIMPLE_FILE_NAME = "09_TemplateTree";
  
  private int currentIndentLevel = 0;
  
  public TemplateTreeReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Protocol");
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Tree structure for template calls.");
    writeLine("Short forms:");
    writeLine("  +--      template call");
    writeLine("  +--SHP   StringHookPoint call");
    writeLine("  +--CHP   CodeHookPoint call");
    writeLine("  +--ASHP  AST StringHookPoint call");
    writeLine("  +--ATHP  AST TemplateHookPoint call");
    writeLine("  +--ACHP  AST CodeHookPoint call");
    writeLine("  +inst    instantiation of java class");
    writeLine("(EOF)");
  }
  
  private String getIndent() {
    String ret = "";
    for (int i = 0; i < currentIndentLevel; i++) {
      ret += INDENTATION;
    }
    return ret;
  }
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    String line = getIndent();
    line += TEMPLATE_CALL_START;
    line += ReportingHelper.getTemplateName(templatename);
    writeLine(line);
    currentIndentLevel++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateEnd(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    currentIndentLevel--;
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    String line = getIndent();
    line += INSTANTIATE_JAVA_CLASS;
    line += ": " + Names.getSimpleName(className);
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetValue(java.lang.String,
   * java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    writeLine("OP set value (key, value): ");
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    callHPS(oldTemplate, afterHPs);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    callHPS(oldTemplate, beforeHPs);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * de.monticore.generating.templateengine.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    callHP(hp);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    callHPS(oldTemplate, hps);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    callSpecificHPS(oldTemplate, hps);
  }
  
  private void callSpecificHPS(String oldTemplate, List<HookPoint> hps) {
    for (HookPoint hp : hps) {
      callSpecificHP(hp);
    }
    
  }
  
  private void callSpecificHP(HookPoint hp) {
    if (hp != null) {
      String line = getIndent();
      if (hp instanceof StringHookPoint) {
        line += SPECIFIC_STRING_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
      else if (hp instanceof TemplateHookPoint) {
        line += SPECIFIC_TEMPLATE_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
      else if (hp instanceof CodeHookPoint) {
        line += SPECIFIC_CODE_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
    }
  }
  
  private void callHPS(String oldTemplate, Collection<HookPoint> hps) {
    for (HookPoint hp : hps) {
      callHP(hp);
    }
  }
  
  private void callHP(HookPoint hp) {
    if (hp != null) {
      String line = getIndent();
      if (hp instanceof StringHookPoint) {
        line += STRING_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
      else if (hp instanceof TemplateHookPoint) {
        line += TEMPLATE_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
      else if (hp instanceof CodeHookPoint) {
        line += CODE_HOOKPOINT;
        line += ": " + getHookPointValue(hp);
        writeLine(line);
      }
    }
  }
  
  private void resetVariables() {
    currentIndentLevel = 0;
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
  public void flush(ASTNode node) {
    writeFooter();
    resetVariables();
    super.flush(node);
  }
  
}
