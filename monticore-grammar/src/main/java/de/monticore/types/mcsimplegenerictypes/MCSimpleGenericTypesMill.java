/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes;

public class MCSimpleGenericTypesMill extends MCSimpleGenericTypesMillTOP {
  /**
   * Initializes a languages Mill.
   * This will also initialize the Mills of all languages it depends on.
   * This ensures that all objects of this mill, such as builders, traversers, scopes, ..., deliver the element of the correct language.
   */
  public static void init() {
    MCSimpleGenericTypesMillTOP.init();

    // Add support for MCCustomTypeArguments to the MCTypeFacade
    MCSimpleGenericTypesMCTypeFacade.initializeAsMCTypeFacade();
  }

  public static void initMe(MCSimpleGenericTypesMill a) {
    MCSimpleGenericTypesMillTOP.initMe(a);
    MCSimpleGenericTypesMCTypeFacade.initializeAsMCTypeFacade();
  }

  public static void reset() {
    MCSimpleGenericTypesMillTOP.reset();
    MCSimpleGenericTypesMCTypeFacade.deinitializeAsMCTypeFacade();
  }
}