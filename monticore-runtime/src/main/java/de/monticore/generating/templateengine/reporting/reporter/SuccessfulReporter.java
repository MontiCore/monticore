/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;

/** *
 * @author MB
 */

public class SuccessfulReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "19_Successful";
  
  public SuccessfulReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportModelStart(de.monticore.ast.ASTNode, java.lang.String, java.lang.String)
   */
  @Override
  public void reportModelStart(ASTNode ast, String modelName, String fileName) {
    reportingHelper.deleteFile(SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportModelEnd(java.lang.String, java.lang.String)
   */
  @Override
  public void reportModelEnd(String modelname, String filename) {
    writeLine("#Successful generation");
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.AReporter#writeHeader()
   */
  @Override
  protected void writeHeader() {
  }
  
  
}
