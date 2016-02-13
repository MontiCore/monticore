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

package de.monticore;

import java.io.File;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.prettyprint.IndentPrinter;

public class ODReporter extends AReporter {
  
  static final String SIMPLE_FILE_NAME = "18_ObjectDiagram";
  
  private String modelName;
  
  private ReportingRepository reporting;
  
  public ODReporter(String outputDir, String modelName, ReportingRepository reporting) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.modelName = modelName;
    this.reporting = reporting;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Object diagram");
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Shows the AST with all attributes as object diagram");
    writeLine("(EOF)");
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    super.flush(ast);
  }

  /**
   * @param ast
   */
  private void writeContent(ASTNode ast) {
    if (ast instanceof ASTGrammarNode) {
      ASTGrammarNode mcNode = (ASTGrammarNode) ast;
      IndentPrinter pp = new IndentPrinter();
      GrammarWithConcepts2OD odPrinter = new GrammarWithConcepts2OD(pp, reporting);
      odPrinter.printObjectDiagram(modelName, mcNode);
      writeLine(pp.getContent());
    }
  }
  
}
