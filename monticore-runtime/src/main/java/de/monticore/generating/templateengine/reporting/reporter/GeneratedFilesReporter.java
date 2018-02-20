/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.se_rwth.commons.Names;

/**
 */
public class GeneratedFilesReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "02_GeneratedFiles";
  
  final static String INDENT = Layouter.getSpaceString(40);
  
  private ReportingRepository repository;
  
  public GeneratedFilesReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Generated Files");
    writeLine("Filename                                AST-Node");
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Generated Files: the list of generated files in the order they are generated.");
    writeLine("Each file knows:");
    writeLine("- Template       responsible for the file creation");
    writeLine("- AST            Node which is passed to the template as ast variable");
    writeLine("- Model Position If the ast node is created as a direct result of parsing a model,");
    writeLine("                 the position of the model element is reported in the form <Line, Column>");
    writeLine("(EOF)");
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String simpleTemplateName = Names.getSimpleName(templatename);
    String file = Names.getSimpleName(qualifiedfilename) + "."
        + fileextension;
    writeLine(file + getIndentAfterFile(file) + repository.getASTNodeNameFormatted(ast));
    writeLine(INDENT + simpleTemplateName + ".ftl");
  }
  
  private String getIndentAfterFile(String file) {
    if (file.length() < INDENT.length() + 1) {
      return INDENT.substring(file.length());
    }
    else {
      return "  ";
    }
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    super.flush(ast);
  }
  
}
