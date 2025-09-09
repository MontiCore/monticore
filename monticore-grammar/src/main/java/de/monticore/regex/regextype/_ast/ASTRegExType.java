// (c) https://github.com/MontiCore/monticore
package de.monticore.regex.regextype._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTRegExType extends ASTRegExTypeTOP {

  public ISymbol definingSymbol = null;

  @Override
  public Optional<? extends ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(definingSymbol);
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }

}
