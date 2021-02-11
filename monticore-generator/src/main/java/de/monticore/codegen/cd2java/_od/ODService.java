/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._od;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractService;

import static de.monticore.codegen.cd2java._od.ODConstants.OD_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKAGE;

public class ODService extends AbstractService<ODService> {

  public ODService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ODService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKAGE;
  }

  @Override
  protected ODService createService(DiagramSymbol cdSymbol) {
    return createODService(cdSymbol);
  }

  public static ODService createODService(DiagramSymbol cdSymbol) {
    return new ODService(cdSymbol);
  }

  public String getODName(ASTCDDefinition astcdDefinition) {
    return astcdDefinition.getName() + OD_SUFFIX;
  }
}
