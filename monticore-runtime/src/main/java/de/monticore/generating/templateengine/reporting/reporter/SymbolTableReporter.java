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

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Names;

import java.io.File;

/**
 * @author BM
 */
public class SymbolTableReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "13_SymbolTable";

  final static int NUM_SPACE = 3;

  final static String INDENT = Layouter.getSpaceString(NUM_SPACE);
  final static String SHORT_INDENT = Layouter.getSpaceString(NUM_SPACE-1);
  final static String SCOPE_START = "+--";
  final static String SYMBOL_START = "<SYM> ";

  private int currentIndentLevel = 0;
  
  public SymbolTableReporter(
      String outputDir,
      String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
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

  private String getIndent() {
    String ret = "";
    for (int i = 0; i < currentIndentLevel; i++) {
      if (i > 0) {
        ret += "|" + SHORT_INDENT;
      }
      else {
        ret += INDENT;
      }
    }
    return ret;
  }

  @Override
  public void reportSymbolTableScope(Scope scope) {
    String line = getIndent();
    line += SCOPE_START;

    if (scope instanceof ArtifactScope) {
      line += "ArtifactScope";
    }
    else {
      line += scope.getName().orElse("<unnamed> " + scope.getClass().getSimpleName() + ":: ");

      if (scope.isSpannedBySymbol()) {
        final ScopeSpanningSymbol spanningSymbol = scope.getSpanningSymbol().get();
        line += " (spanning kind " + Names.getSimpleName(spanningSymbol.getKind().getName()) + ")";
      }
    }

    writeLine(line);

    currentIndentLevel++;

    scope.getSymbols().stream()
        .filter(sym -> !(sym instanceof ScopeSpanningSymbol))
        .forEach(this::reportSymbol);


    scope.getSubScopes().forEach(this::reportSymbolTableScope);
    currentIndentLevel--;
  }

  private void reportSymbol(Symbol sym) {
    String line = getIndent();
    line += SYMBOL_START;

    line += sym.getName() + " (kind " + Names.getSimpleName(sym.getKind().getName()) + ")";
    writeLine(line);
  }
}
