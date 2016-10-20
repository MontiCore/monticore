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
import java.util.Collection;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * @author BM
 */
public class SymbolTableReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "13_SymbolTable";
  
  final static int NUM_SPACE = 2;
  
  final static String INDENT = Layouter.getSpaceString(NUM_SPACE);
  
  private final String outputDir;
  
  private final String modelName;
  
  private final ReportingRepository repository;
  
  private int currentIndentLevel = 0;
  
  public SymbolTableReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.outputDir = outputDir;
    this.modelName = modelName;
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("//========================================================== Symbol Table");
  }
  
  private void writeFooter() {
    writeLine("//========================================================== Explanation");
    writeLine("//Shows symbol table after finishing the generation process.");
  }
  
  @Override
  public void flush(ASTNode ast) {
    Optional<? extends Scope> scope = ast.getSpannedScope();
    if (scope.isPresent()) {
      String name = scope.get().getName().orElse("");
      writeLine("objectdiagram " + name + " {");
      currentIndentLevel++;
      reportSymbolTableScope(scope.get());
      currentIndentLevel--;
      writeLine("}");
    }
    writeFooter();
    super.flush(ast);
  }
  
  protected String getIndent() {
    String ret = "";
    for (int i = 0; i < currentIndentLevel; i++) {
      ret += INDENT;
    }
    return ret;
  }
  
  @Override
  public void reportSymbolTableScope(Scope scope) {
    
    final Collection<Symbol> symbols = Scopes.getLocalSymbolsAsCollection(scope);
    String type;
    if (scope.getSpanningSymbol().isPresent()) {
      type = Names.getSimpleName(scope.getSpanningSymbol().get().getKind().getName());
      int i = type.indexOf('$');
      if (i > 0) {
        type = type.substring(0, i);
      }      
    }
    else {
      type = Names.getSimpleName(scope.getClass().getName());
    }
    
    String line = getIndent() + StringTransformations.uncapitalize(scope.getName().orElse("")) + ": " + type + "{";
    writeLine(line);
    currentIndentLevel++;
    
    if (scope.getSpanningSymbol().isPresent()) {
      reportAttributes(scope.getSpanningSymbol().get());
    }
    String delim = "";
    for (Symbol symbol : symbols) {
      if (!(symbol instanceof ScopeSpanningSymbol)) {
        writeLine(delim);
        delim = ",";
        reportSymbol(symbol);
      }
    }
    
    for (Scope subScope : scope.getSubScopes()) {
      writeLine(delim);
      delim = ",";
      reportSymbolTableScope(subScope);
    }
    currentIndentLevel--;
    line = getIndent() + "}";
    writeLine(line);
  }
  
  protected void reportSymbol(Symbol sym) {
    String line = getIndent();
    String type = Names.getSimpleName(sym.getKind().getName());
    int i = type.indexOf('$');
    if (i > 0) {
      type = type.substring(0, i);
    }
    line += StringTransformations.uncapitalize(sym.getName()) + ": " + type + " {";
    writeLine(line);
    currentIndentLevel++;
    
    reportAttributes(sym);
    currentIndentLevel--;
    writeLine(getIndent() + "}");
    
  }
  
  protected void reportAttributes(Symbol sym) {
    String line = getIndent();
    line += "name = \"" + sym.getName() + "\";";
    writeLine(line);
    if (sym.getAstNode().isPresent()) {
      line = getIndent();
      line += "astNode = ";
      line += repository.getASTNodeNameFormatted(sym.getAstNode().get());
      line += ";";
      writeLine(line);
    }
    if (!sym.getAccessModifier().equals(AccessModifier.ALL_INCLUSION)) {
      writeLine(getIndent() + "accesModifier = " + sym.getAccessModifier().toString() + ";");
    }
    
  }
}
