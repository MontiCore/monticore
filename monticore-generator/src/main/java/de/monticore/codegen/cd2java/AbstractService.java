package de.monticore.codegen.cd2java;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public abstract class AbstractService {

  private final ASTCDCompilationUnit compilationUnit;

  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  protected ASTCDCompilationUnit getCD() {
    return this.compilationUnit;
  }

  protected CDSymbol getCDSymbol() {
    return (CDSymbol) getCD().getCDDefinition().getSymbol();
  }

  protected String getCDName() {
    return getCD().getCDDefinition().getName();
  }

  protected String getBasePackage() {
    return String.join(".", getCD().getPackageList());
  }

  public String getPackage() {
    return (String.join(getBasePackage(), getSubPackage(), getCDName())).toLowerCase();
  }

  protected abstract String getSubPackage();
}
