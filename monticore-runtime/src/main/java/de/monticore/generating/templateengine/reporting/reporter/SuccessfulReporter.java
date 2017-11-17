/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
