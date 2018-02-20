/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

/**
 * Represents the absence of access modifiers in a grammarlanguage.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public final class NoAccessModifier implements AccessModifier {
  
  public static final NoAccessModifier INSTANCE = new NoAccessModifier();
  
  private NoAccessModifier() {
    
  }
  
  /**
   * @see de.monticore.symboltable.modifiers.AccessModifier#includes(de.monticore.symboltable.modifiers.AccessModifier)
   */
  public boolean includes(AccessModifier modifier) {
    return true;
  }
  
}
