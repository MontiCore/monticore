/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.unittypes._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTMinuteType extends ASTMinuteTypeTOP {

  protected ISymbol definingSymbol;

  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }
}
