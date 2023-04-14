/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import java.util.Map;

public final class NoAccessModifier implements AccessModifier {

  public static final NoAccessModifier INSTANCE = new NoAccessModifier();

  private NoAccessModifier() {

  }

  /**
   * @see AccessModifier#includes(AccessModifier)
   */
  public boolean includes(AccessModifier modifier) {
    return true;
  }

  @Override
  public Map<String, AccessModifier> getDimensionToModifierMap() {
    return Map.of(ALL, this);
  }
}
