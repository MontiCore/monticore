/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

/**
 * Loads a symbol
 */
public interface ISymbolLoader {

  /**
   * @return the symbol name
   */
  String getName();

  /**
   * @return the symbol
   */
  ISymbol getLoadedSymbol();

  /**
   * @return true, if the referenced symbol is loaded
   */
  boolean isSymbolLoaded();

  /**
   * @return the enclosing scope of the reference itself
   */
  IScope getEnclosingScope();

}
