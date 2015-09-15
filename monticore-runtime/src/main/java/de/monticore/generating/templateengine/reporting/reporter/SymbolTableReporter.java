/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.symboltable.CommonScope;

/**
 * @author BM
 */
public class SymbolTableReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "13_SymbolTable";
  
  final static String INDENT = Layouter.getSpaceString(40);
  
  public SymbolTableReporter(
      String outputDir,
      String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportSymbolTable(de.monticore.symboltable.CommonScope)
   */
  @Override
  public void reportSymbolTable(CommonScope scope) {
    writeLine(scope.toString());
  }

  @Override
  protected void writeHeader() {
    writeLine("========================================================== Symbol Table");
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Reporter is not implemented yet.");
    writeLine("Shows symbol table after finishing the generation process.");
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeFooter();
    super.flush(ast);
  }
  
}
