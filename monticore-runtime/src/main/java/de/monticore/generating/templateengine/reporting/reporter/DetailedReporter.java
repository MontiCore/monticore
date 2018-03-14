/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.se_rwth.commons.Names;

/**
 */
public class DetailedReporter extends AReporter {
  
  final static String GENERATED_FILE_OPENED = "+file";
  
  final static String GENERATED_FILE_CLOSED = "-file";
  
  final static String TEMPLATE_CALLED = "+tpl";
  
  final static String TEMPLATE_ENDED = "-tpl";
  
  final static String INSTANTIATE_JAVA_CLASS = "inst";
  
  final static String SET_GLOBAL_VARIABLE = "setv";
  
  final static String ADD_GLOBAL_VARIABLE = "addv";
  
  final static String SIMPLE_FILE_NAME = "08_Detailed";
  
  final static String ERROR = "err";
  
  final static String WARNING = "warn";
  
  private ReportingRepository repository;
  
  private int templateDepth = 0;
  
  public DetailedReporter(String outputDir, String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Detailed Report");
  }
  
  private void writeFooter() {
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
    // writeLine("* the current depth of the template hierarchy (xT)");
    writeLine("* the current depth of the template hierarchy (xT)");
    writeLine("(EOF)");
  }
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    templateDepth++;
    String startString = TEMPLATE_CALLED + getLineStart(ast);
    String line = startString
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - startString.length());
    
    line += ReportingHelper.getTemplateName(templatename)
        + Layouter
            .getSpaceString(ReportingConstants.COLUMN
                - ReportingHelper.getTemplateName(templatename).length());
    line += valueStr(ast);
    writeLine(line);
  }
  
  /**
   * Uses ast2idents to print a compact version for any form of object
   * 
   * @param o
   * @return usable representation (one liner)
   */
  public String valueStr(ASTNode ast) {
    return repository.getASTNodeNameFormatted(ast);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateEnd(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    String startString = TEMPLATE_ENDED + getLineStart(ast);
    String line = startString
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - startString.length());
    
    line += ReportingHelper.getTemplateName(templatename)
        + Layouter
            .getSpaceString(ReportingConstants.COLUMN
                - ReportingHelper.getTemplateName(templatename).length());
    line += valueStr(ast);
    writeLine(line);
    templateDepth--;
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    String line = INSTANTIATE_JAVA_CLASS
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - INSTANTIATE_JAVA_CLASS.length());
    line += className + Layouter
        .getSpaceString(ReportingConstants.COLUMN - className.length());
    line += params;
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetValue(java.lang.String,
   * java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    String line = SET_GLOBAL_VARIABLE
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - SET_GLOBAL_VARIABLE.length());
    line += name;
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportAddValue(java.lang.String,
   * java.lang.Object, int)
   */
  @Override
  public void reportAddValue(String name, Object value, int size) {
    String line = ADD_GLOBAL_VARIABLE
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - ADD_GLOBAL_VARIABLE.length());
    line += name;
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportWarning(java.lang.String)
   */
  @Override
  public void reportWarning(String message) {
    String line = WARNING
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - WARNING.length());
    line += message;
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportError(java.lang.String)
   */
  @Override
  public void reportError(String message) {
    String line = ERROR
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - ERROR.length());
    line += message;
    writeLine(line);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String name = Names.getSimpleName(qualifiedfilename) + "." + fileextension;
    String line = GENERATED_FILE_OPENED
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - GENERATED_FILE_OPENED.length());
    line += name + Layouter
        .getSpaceString(ReportingConstants.COLUMN - name.length());
    
    line += valueStr(ast);
    String secondline = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2)
        + ReportingHelper.getTemplateName(templatename);
    writeLine(line);
    writeLine(secondline);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileFinalization(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileFinalization(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String name = qualifiedfilename + "." + fileextension;
    String line = GENERATED_FILE_CLOSED
        + Layouter
            .getSpaceString(ReportingConstants.FORMAT_LENGTH_2 - GENERATED_FILE_CLOSED.length());
    line += name + Layouter
        .getSpaceString(ReportingConstants.COLUMN - name.length());
    
    line += ReportingHelper.getTemplateName(templatename);
    String secondline = Layouter.getSpaceString(ReportingConstants.FORMAT_LENGTH_2) + valueStr(ast);
    writeLine(line);
    writeLine(secondline);
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
  
  private void resetVariables() {
    templateDepth = 0;
  }
  
  private String getLineStart(ASTNode node) {
    String lineStart = "(" + templateDepth + "T)";
    return lineStart;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportDetailed(java.lang.String)
   */
  @Override
  public void reportDetailed(String value) {
    // String line = calculateLine(value);
    writeLine(value);
  }
  
  private static String calculateLine(String value) {
    String line = ReportingHelper.formatLineToReportingLine(value,
        ReportingConstants.REPORTING_ROW_LENGTH);
    return line;
  }
  
  public static void main(String[] args) {
    System.out
        .println(calculateLine("looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong\n\n\n\n\n\nloooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"));
  }
  
}
