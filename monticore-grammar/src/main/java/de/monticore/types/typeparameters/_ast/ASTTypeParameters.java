/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typeparameters._ast;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.typeparameters._symboltable.ITypeParametersScope;

public class ASTTypeParameters extends ASTTypeParametersTOP {

  /**
   * Removes the typevar symbols in the current scope
   * and moves them into the specified one.
   */
  public void moveSymbolsIntoScope(IBasicSymbolsScope newScope) {
    ITypeParametersScope oldScope = getEnclosingScope();
    for (ASTTypeParameter param : getTypeParameterList()) {
      TypeVarSymbol sym = param.getSymbol();
      oldScope.remove(sym);
      newScope.add(sym);
    }
  }

}
