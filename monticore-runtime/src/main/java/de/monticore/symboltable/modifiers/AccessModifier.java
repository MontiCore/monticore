/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AccessModifier extends Modifier {

  /**
   * Access modifier that includes all other access modifiers. It can be used
   * to resolve symbols having any access modifier.
   */
  AccessModifier ALL_INCLUSION = new AllInclusionAccessModifier();

  boolean includes(AccessModifier modifier);

  Map<String, AccessModifier> getDimensionToModifierMap();

  default CompoundAccessModifier shallowCopy() {
    return new CompoundAccessModifier(
         new ArrayList<>(getDimensionToModifierMap().values())
    );
  }

  String ALL = "All";

  final class AllInclusionAccessModifier implements AccessModifier {
    @Override
    public boolean includes(AccessModifier modifier) {
      return true;
    }

    private AllInclusionAccessModifier() {
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(ALL, this);
    }
  }

}
