/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

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

    private AllInclusionAccessModifier() {
    }
  }

}
