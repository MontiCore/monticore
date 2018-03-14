/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types.references;

import de.monticore.symboltable.types.TypeSymbol;

/**
 * Represents an actual type argument as in Java.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ActualTypeArgument {

  private final boolean isLowerBound;
  private final boolean isUpperBound;

  // TODO Handle the case List<? extends Bla & Blub>: This is just ONE type argument with several upper bounds
  private final TypeReference<? extends TypeSymbol> type;

  // TODO make generic, <T extends TypeSymbol>
  public ActualTypeArgument(boolean isLowerBound, boolean isUpperBound, TypeReference<? extends TypeSymbol> type) {
    this.isLowerBound = isLowerBound;
    this.isUpperBound = isUpperBound;
    this.type = type;
  }

  public ActualTypeArgument(TypeReference<? extends TypeSymbol> type) {
    this(false, false, type);
  }

  public boolean isLowerBound() {
    return isLowerBound;
  }

  public boolean isUpperBound() {
    return isUpperBound;
  }

  public TypeReference<? extends TypeSymbol> getType() {
    return type;
  }
}
