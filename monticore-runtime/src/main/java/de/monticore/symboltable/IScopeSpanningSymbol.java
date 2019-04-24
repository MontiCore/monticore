/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

public interface IScopeSpanningSymbol extends ISymbol {

  /**
   * @return the scope spanned by this symbol.
   */
  IScope getSpannedScope();

}
