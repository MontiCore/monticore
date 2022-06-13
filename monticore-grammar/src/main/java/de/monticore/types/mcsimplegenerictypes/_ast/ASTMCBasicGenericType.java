/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTMCBasicGenericType extends ASTMCBasicGenericTypeTOP {

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
