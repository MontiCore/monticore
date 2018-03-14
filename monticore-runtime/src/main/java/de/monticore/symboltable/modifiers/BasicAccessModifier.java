/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

/**
 * Contains constants for basic access modifiers that exist in Java, i.e., public, protected,
 * package-local and private. Additional, the constant {@link #ABSENT} represents the absent of
 * modifiers.
 *
 * @author Pedram Mir Seyed Nazari
 */
public enum BasicAccessModifier implements AccessModifier {
  
  PUBLIC {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return modifier.equals(PUBLIC);
    }
    
    @Override
    public String toString() {
      return "public";
    }
  },
  
  PROTECTED {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC) || modifier.equals(PROTECTED));
    }
    
    @Override
    public String toString() {
      return "protected";
    }
  },
  
  PACKAGE_LOCAL {
    
    @Override
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC)
          || modifier.equals(PROTECTED)
          || modifier.equals(PACKAGE_LOCAL));
    }
    
    @Override
    public String toString() {
      return "package_local";
    }   
  },
  
  PRIVATE {
    
    public boolean includes(AccessModifier modifier) {
      return (modifier.equals(PUBLIC)
          || modifier.equals(PROTECTED)
          || modifier.equals(PACKAGE_LOCAL)
          || modifier.equals(PRIVATE));
    }

    @Override
    public String toString() {
      return "private";
    }    
 },
}
