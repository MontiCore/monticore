/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.*;
import de.se_rwth.commons.Names;

import java.io.File;
import java.util.List;

public class DetailedReporter extends AReporter {
  
  static final String GENERATED_FILE_OPENED = "+file";
  
  static final String GENERATED_FILE_CLOSED = "-file";
  
  static final String TEMPLATE_CALLED = "+tpl";
  
  static final String TEMPLATE_ENDED = "-tpl";
  
  static final String INSTANTIATE_JAVA_CLASS = "inst";
  
  static final String SET_GLOBAL_VARIABLE = "setv";
  
  static final String ADD_GLOBAL_VARIABLE = "addv";
  
  static final String SIMPLE_FILE_NAME = "08_Detailed";
  
  static final String ERROR = "err";
  
  static final String WARNING = "warn";
  
  protected ReportingRepository repository;
  
  protected int templateDepth = 0;
  
  public DetailedReporter(String outputDir, String modelName,
      ReportingRepository repository) {
    super(outputDir
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Detailed Report");
  }
  
  protected void writeFooter() {
    writeLine("");
    writeLine("");
    writeLine("========================================================== Explanation");
    writeLine("");
    writeLine("A fine grained report of all the events happening. The events are the following:");
    writeLine("warn        issued warning");
    writeLine("err         issued error");
    writeLine("inst        instantiation");
    writeLine("setv        ... variable assignment");
    writeLine("addv        ... this is an extension (addValue), the extension is shown only");
    writeLine("+file       generated file is opened");
    writeLine("-file       ... closed");
    writeLine("+tpl        template called/included");
    writeLine("-tpl        ... ends");
    writeLine("set         hook point assignment");
    writeLine("setr        template replacement by hook points");
    writeLine("setb        assignment of hook points that are called before the template ");
    writeLine("seta        assignment of hook points that are called after the template ");
    writeLine("call        hook point execution");
    writeLine("callr       execution of hook points that replaced a template");
    writeLine("callb       execution of hook points that are called before the template");
    writeLine("calla       execution of hook points that are called after the template");
    writeLine("");
    writeLine("hook points are sepreated into");
    writeLine("* SHP       string hook point");
    writeLine("* THP       template hook point");
    writeLine("* CHP       code hook point");
    writeLine("* EHP       empty hook point");
    writeLine("* ASHP      AST string hook point");
    writeLine("* ATHP      AST template hook point");
    writeLine("* ACHP      AST code hook point");
    writeLine("");
    writeLine("for tpl events each line comes with");
    writeLine("* the shortname of the template");
    writeLine("* the AST it operates on");
    writeLine("* the current depth of the template hierarchy (xT)");
    writeLine("(EOF)");
  }
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    templateDepth++;
    this.reportTemplateAction(ast, templatename, TEMPLATE_CALLED);
  }
  
  /**
   * Uses ast2idents to print a compact version for any form of object
   *
   * @param ast node to be printed
   * @return usable representation (one liner)
   */
  public String valueStr(ASTNode ast) {
    return repository.getASTNodeNameFormatted(ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportTemplateEnd(java.lang.String,
   *     de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    this.reportTemplateAction(ast, templatename, TEMPLATE_ENDED);
    templateDepth--;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
   *     java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    String line = INSTANTIATE_JAVA_CLASS + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - INSTANTIATE_JAVA_CLASS.length());
    line += className + Layouter.getSpaceString(
        ReportingConstants.COLUMN - className.length());
    line += params;
    writeLine(line);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportSetValue(java.lang.String,
   *     java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    String line = SET_GLOBAL_VARIABLE + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - SET_GLOBAL_VARIABLE.length());
    line += name;
    writeLine(line);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportAddValue(java.lang.String,
   *     java.lang.Object, int)
   */
  @Override
  public void reportAddValue(String name, Object value, int size) {
    String line = ADD_GLOBAL_VARIABLE + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - ADD_GLOBAL_VARIABLE.length());
    line += name;
    writeLine(line);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportWarning(java.lang.String)
   */
  @Override
  public void reportWarning(String message) {
    String line = WARNING + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - WARNING.length());
    line += message;
    writeLine(line);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportError(java.lang.String)
   */
  @Override
  public void reportError(String message) {
    String line = ERROR + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - ERROR.length());
    line += message;
    writeLine(line);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   *     java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String name = Names.getSimpleName(qualifiedfilename) + "." + fileextension;
    String line = GENERATED_FILE_OPENED + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - GENERATED_FILE_OPENED.length());
    line += name + Layouter.getSpaceString(
        ReportingConstants.COLUMN - name.length());
    
    line += valueStr(ast);
    String secondline =
        Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2)
            + ReportingHelper.getTemplateName(templatename);
    writeLine(line);
    writeLine(secondline);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportFileFinalization(java.lang.String,
   *     java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileFinalization(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String name = qualifiedfilename + "." + fileextension;
    String line = GENERATED_FILE_CLOSED + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - GENERATED_FILE_CLOSED.length());
    line += name + Layouter.getSpaceString(
        ReportingConstants.COLUMN - name.length());
    
    line += ReportingHelper.getTemplateName(templatename);
    String secondline =
        Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2)
            + valueStr(ast);
    writeLine(line);
    writeLine(secondline);
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
  
  protected void resetVariables() {
    templateDepth = 0;
  }
  
  protected String getLineStart(ASTNode node) {
    return "(" + templateDepth + "T)";
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportDetailed(java.lang.String)
   */
  @Override
  public void reportDetailed(String value) {
    // String line = calculateLine(value);
    writeLine(value);
  }
  
  protected static String calculateLine(String value) {
    return ReportingHelper.formatLineToReportingLine(value,
        ReportingConstants.REPORTING_ROW_LENGTH);
  }
  
  private void reportTemplateAction(ASTNode astNode, String templatename,
      String actionType) {
    String startString = actionType + getLineStart(astNode);
    String line = startString + Layouter.getSpaceString(
        ReportingConstants.FORMAT_LENGTH_2 - startString.length());
    
    line +=
        ReportingHelper.getTemplateName(templatename) + Layouter.getSpaceString(
            ReportingConstants.COLUMN
                - ReportingHelper.getTemplateName(templatename).length());
    line += valueStr(astNode);
    writeLine(line);
  }
  
}
