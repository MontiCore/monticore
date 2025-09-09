package de.monticore.siunit.siunittypes4computing._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTSIUnitType4Computing extends ASTSIUnitType4ComputingTOP{

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
