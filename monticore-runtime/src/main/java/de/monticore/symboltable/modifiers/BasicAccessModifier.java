/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import java.util.Map;

public enum BasicAccessModifier implements AccessModifier {

  PUBLIC {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier visibility = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(visibility != null){
        return visibility.equals(PUBLIC);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }

    @Override
    public String toString() {
      return "public";
    }


  },

  PROTECTED {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier visibility = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(visibility != null){
        return (visibility.equals(PUBLIC)
          || visibility.equals(PROTECTED));
      }
      return true;
    }

    @Override
    public String toString() {
      return "protected";
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  },

  PACKAGE_LOCAL {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier visibility = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(visibility != null){
        return (visibility.equals(PUBLIC)
          || visibility.equals(PROTECTED)
          || visibility.equals(PACKAGE_LOCAL));
      }
      return true;
    }

    @Override
    public String toString() {
      return "package_local";
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  },

  PRIVATE {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier visibility = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(visibility != null){
        return (visibility.equals(PUBLIC)
          || visibility.equals(PROTECTED)
          || visibility.equals(PACKAGE_LOCAL)
          || visibility.equals(PRIVATE));
      }
      return true;
    }

    @Override
    public String toString() {
      return "private";
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }

  },
  ;

  public static final String DIMENSION = "Visibility";
}
