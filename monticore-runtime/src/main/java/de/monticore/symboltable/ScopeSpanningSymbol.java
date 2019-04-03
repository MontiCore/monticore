/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

@Deprecated
public interface ScopeSpanningSymbol extends Symbol {

  /**
   * @return the scope spanned by this symbol.
   */
  Scope getSpannedScope();

}
