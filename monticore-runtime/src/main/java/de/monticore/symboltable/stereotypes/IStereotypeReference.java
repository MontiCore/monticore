/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import java.util.Optional;

/**
 * Reference to a {@link IStereotypeSymbol} that may be invalid if it does not
 * actually refer to a stereotype symbol.
 */
public interface IStereotypeReference {
  Optional<? extends IStereotypeSymbol> getResolved();
}
