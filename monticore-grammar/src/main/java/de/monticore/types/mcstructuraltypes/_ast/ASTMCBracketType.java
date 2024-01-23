package de.monticore.types.mcstructuraltypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTMCBracketType extends ASTMCBracketTypeTOP {

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
