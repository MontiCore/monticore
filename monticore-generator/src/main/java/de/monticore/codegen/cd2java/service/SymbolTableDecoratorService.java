package de.monticore.codegen.cd2java.service;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class SymbolTableDecoratorService {

  private static final String SYMBOL_TABLE_PACKAGE = "._symboltable.";

  private final ASTCDCompilationUnit compilationUnit;

  public SymbolTableDecoratorService(final ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  public String getSymbolTablePackage() {
    return (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + SYMBOL_TABLE_PACKAGE).toLowerCase();
  }
}
