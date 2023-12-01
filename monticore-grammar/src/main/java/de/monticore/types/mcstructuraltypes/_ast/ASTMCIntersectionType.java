package de.monticore.types.mcstructuraltypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTMCIntersectionType extends ASTMCIntersectionTypeTOP {

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
