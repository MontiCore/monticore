/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.modifiers;

import java.util.Map;

public enum StaticAccessModifier implements AccessModifier {

  STATIC {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier staticModifier = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(staticModifier != null){
        return staticModifier.equals(STATIC);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }

    @Override
    public String toString() {
      return "static";
    }

  },

  NON_STATIC {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier staticModifier = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(staticModifier != null){
        return staticModifier.equals(NON_STATIC);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }

    @Override
    public String toString() {
      return "";
    }
  }
  ;

  public static final String DIMENSION = "Static";
}
