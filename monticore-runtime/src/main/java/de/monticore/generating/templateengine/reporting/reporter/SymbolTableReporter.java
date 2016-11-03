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
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;

/**
 * @author BM
 */
public class SymbolTableReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "13_SymbolTable";
  
  public static final String LAYOUT_FULL = "@%s!Symbol";
    
  private final String outputDir;
  
  private final String modelName;
  
  private final ReportingRepository repository;
    
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
      reportSymbolTableScope(scope.get());
    }
    writeFooter();
    super.flush(ast);
  }
    
  protected void reportScope(Scope scope, IndentPrinter printer) {    
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
    String scopeName = format(scope.getName().orElse(""));
    printer.println(scopeName + ": " + type + "{");
    printer.indent();
    
    if (scope.getSpanningSymbol().isPresent()) {
      reportAttributes(scope.getSpanningSymbol().get(), printer);
    }
    
    for (Symbol symbol : symbols) {
      if (!(symbol instanceof ScopeSpanningSymbol)) {
        reportSymbol(symbol, printer);
      }
    }
    
    for (Scope subScope : scope.getSubScopes()) {
      reportScope(subScope, printer);
      printer.println(";");
    }
    
    printer.unindent();
    printer.print("}");
  }
  
  @Override
  public void reportSymbolTableScope(Scope scope) {
    IndentPrinter printer = new IndentPrinter();
    String name = scope.getName().orElse("");
    printer.println("objectdiagram " + name + " {");
    printer.indent();
    reportScope(scope, printer);
    printer.println();
    printer.unindent();
    printer.println("}");
    writeLine(printer.getContent());
  }
  
  protected void reportSymbol(Symbol sym, IndentPrinter printer) {
    String type = Names.getSimpleName(sym.getKind().getName());
    int i = type.indexOf('$');
    if (i > 0) {
      type = type.substring(0, i);
    }
    
    String symName = format(sym.getName());
    printer.println(symName + ": " + type + " {");    
    printer.indent();  
    reportAttributes(sym, printer);
    printer.unindent();   
    printer.println("};");    
  }
  
  protected void reportAttributes(Symbol sym, IndentPrinter printer) {
    printer.println("name = \"" + sym.getName() + "\";");
    if (sym.getAstNode().isPresent()) {
      printer.print("astNode = ");
      printer.print(repository.getASTNodeNameFormatted(sym.getAstNode().get()));
      printer.println(";");
    }
    if (!sym.getAccessModifier().equals(AccessModifier.ALL_INCLUSION)) {
      printer.println("accesModifier = \"" + sym.getAccessModifier().toString() + "\";");
    }
  }
  
  protected String format(String name) {
    return String.format(LAYOUT_FULL, name);
  }
  
}
