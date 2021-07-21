/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.se_rwth.commons.Names;

import java.io.File;

/**
 */
public class GeneratedFilesReporter extends AReporter {
  
  static final String SIMPLE_FILE_NAME = "02_GeneratedFiles";
  
  static final String INDENT = Layouter.getSpaceString(40);
  
  protected ReportingRepository repository;
  
  public GeneratedFilesReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Generated Files");
    writeLine("Filename                                AST-Node");
  }
  
  protected void writeFooter() {
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
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
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
  
  protected String getIndentAfterFile(String file) {
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
