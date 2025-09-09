/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.List;
import java.util.Optional;

public class ASTMCQualifiedType extends ASTMCQualifiedTypeTOP {

  protected ISymbol definingSymbol;

  @Override
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }

  public List<String> getNameList() {
    return this.getMCQualifiedName().getPartsList();
  }
}
