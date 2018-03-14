/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

/**
 * Super type for access modifiers, such as <code>public</code> and <code>protected</code>.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public interface AccessModifier extends Modifier {

  /**
   * Access modifier that includes all other access modifiers. It can be used
   * to resolve symbols having any access modifier.
   */
  AccessModifier ALL_INCLUSION = new AllInclusionAccessModifier();

  boolean includes(AccessModifier modifier);


  final class AllInclusionAccessModifier implements AccessModifier {
    @Override
    public boolean includes(AccessModifier modifier) {
      return true;
    }

    private AllInclusionAccessModifier(){
    }
  }
  
}
