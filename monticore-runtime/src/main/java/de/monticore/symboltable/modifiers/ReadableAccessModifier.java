/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.modifiers;

import java.util.Map;

public enum ReadableAccessModifier implements AccessModifier {

  READABLE {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier readable = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(readable != null){
        return readable.equals(READABLE);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  },

  NON_READABLE {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier readable = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(readable != null){
        return readable.equals(NON_READABLE);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  }
  ;

  public static final String DIMENSION = "Readable";
}
