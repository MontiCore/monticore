/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;

/**
 */
public class TransformationReporter extends AReporter {

  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTransformationStart(java.lang.String)
   */
  @Override
  public void reportTransformationStart(String transformationName) {
    writeLine("Start Trafo: " + transformationName);
  }

  final static String SIMPLE_FILE_NAME = "14_Transformations";

  final static String INDENT = "                                        ";

  private ReportingRepository repository;

  public TransformationReporter(
      String outputDir,
      String modelName,
      ReportingRepository repo) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName, SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repo;
  }

  @Override
  public void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine(transformationName + getIndentAfterFile(transformationName) + formattedName
        + getIndentAfterFile(formattedName) + "added");
  }

  @Override
  public void reportTransformationObjectChange(String transformationName, ASTNode ast,
      String attributeName) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine(transformationName + getIndentAfterFile(transformationName) + formattedName
        + getIndentAfterFile(formattedName) +attributeName + " changed");
  }

  @Override
  public void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine(transformationName + getIndentAfterFile(transformationName) + formattedName
        + getIndentAfterFile(formattedName) + "deleted");
  }

  @Override
  public void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine(transformationName + getIndentAfterFile(transformationName) + formattedName
        + getIndentAfterFile(formattedName) + "matched");
  }

  @Override
  public void reportTransformationOldValue(String transformationName, ASTNode ast) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "old value: " + formattedName );
  }

  @Override
  public void reportTransformationNewValue(String transformationName, ASTNode ast) {
    String formattedName = repository.getASTNodeNameFormatted(ast);
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "new value: " + formattedName );
  }

  @Override
  public void reportTransformationOldValue(String transformationName, String value) {
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "old value: " + value );
  }

  @Override
  public void reportTransformationNewValue(String transformationName, String value) {
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "new value: " + value );
  }

  @Override
  public void reportTransformationOldValue(String transformationName, boolean value) {
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "old value: " + value );
  }

  @Override
  public void reportTransformationNewValue(String transformationName, boolean value) {
    writeLine("   " + getIndentAfterFile("") + getIndentAfterFile("") + "new value: " + value );
  }

  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    super.flush(ast);
  }

  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Applied Transformations: the list of transformations in the order they are applied.");
    writeLine("(EOF)");
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
  protected void writeHeader() {
    writeLine("========================================================== Applied Transformations");
    writeLine("Transformation Name                     AST-Node                                Transformation Type");
  }



}
