/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfunctiontypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTMCFunctionTypeNoParentheses
    extends ASTMCFunctionTypeNoParenthesesTOP {

  protected ISymbol definingSymbol;

  @Override
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }
}
