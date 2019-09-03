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
   * Referenced symbol eg. Name@State -> String name; attribute is a referenced Symbol
   */
  REFERENCED_SYMBOL("referencedSymbol"),
  /**
   * Symbol to which is referenced eg. Name@State -> Optional<StateSymbol> nameSymbol;
   */
  REFERENCED_SYMBOL_ATTRIBUTE("referencedSymbolAttribute"),
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
  METHOD_BODY("methodBody"),
  /**
   * To add bodies to methods
   */
  AST_TYPE("astType"),
  /**
   * shows if a prod is the start prod in a grammar
   */
  START_PROD("startProd");

  private final String stereotype;

  private MC2CDStereotypes(String stereotype) {
    this.stereotype = stereotype;
  }

  @Override
  public String toString() {
    return stereotype;
  }

}
