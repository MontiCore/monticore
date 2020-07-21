// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._od;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;

import static de.monticore.codegen.cd2java._od.ODConstants.OD_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class ODService extends AbstractService<ODService> {

  public ODService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ODService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKAGE;
  }

  @Override
  protected ODService createService(CDDefinitionSymbol cdSymbol) {
    return createODService(cdSymbol);
  }

  public static ODService createODService(CDDefinitionSymbol cdSymbol) {
    return new ODService(cdSymbol);
  }

  public String getODName(ASTCDDefinition astcdDefinition) {
    return astcdDefinition.getName() + OD_SUFFIX;
  }
}
