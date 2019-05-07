/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;


/**
 * Class diagram stereotypes used by MC grammar -> CD transformation
 *
 */
public enum MC2CDStereotypes {
  /**
   * The rule attribute is defined in a super grammar
   */
  INHERITED("inherited"),
  /**
   * Type defined in the Java language
   */
  EXTERNAL_TYPE("externalType"),
  /**
   * Referenced symbol
   */
  REFERENCED_SYMBOL("referencedSymbol"),
  /**
   * To add default implementation to method
   */
  DEFAULT_IMPLEMENTATION("defaultImplementation"),
  /**
   * To mark class as symbol
   */
  SYMBOL("symbol"),
  /**
   * To mark class as scope
   */
  SCOPE("scope"),
  /**
   * To add bodies to methods
   */
  METHOD_BODY("methodBody");

  private final String stereotype;
  
  private MC2CDStereotypes(String stereotype) {
    this.stereotype = stereotype;
  }
  
  @Override
  public String toString() {
    return stereotype;
  }
  
}
