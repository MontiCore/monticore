package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

public abstract class AbstractService {

  private final ASTCDCompilationUnit compilationUnit;

  private final CDTypeFactory cdTypeFactory;

  public AbstractService(final ASTCDCompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
  }

  protected ASTCDCompilationUnit getCD() {
    return this.compilationUnit;
  }

  protected CDTypeFactory getCDTypeFactory() {
    return this.cdTypeFactory;
  }

  protected CDSymbol getCDSymbol() {
    return (CDSymbol) getCD().getCDDefinition().getSymbol();
  }

  public String getCDName() {
    return getCD().getCDDefinition().getName();
  }

  public String getBasePackage() {
    return String.join(".", getCD().getPackageList()).toLowerCase();
  }

  public String getPackage() {
    return String.join(".", getBasePackage(), getCDName(), getSubPackage()).toLowerCase();
  }

  public String getPackage(CDSymbol cd) {
    return String.join(".", cd.getPackageName(), cd.getName(), getSubPackage()).toLowerCase();
  }

  protected abstract String getSubPackage();
}
