/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import java.util.Optional;

/**
 * An {@link IStereotypeReference} that is directly backed by a symbol.
 */
public final class SymbolBackedStereotypeReference implements IStereotypeReference {
  private final IStereotypeSymbol symbol;

   public  SymbolBackedStereotypeReference(IStereotypeSymbol stereotype) {
     this.symbol = stereotype;
   }

  @Override
  public Optional<IStereotypeSymbol> getResolved() {
    return Optional.of(symbol);
  }
}
