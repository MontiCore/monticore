/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface ScopeSpanningSymbol extends Symbol {

  /**
   *
   * @return the scope spanned by this symbol.
   */
  Scope getSpannedScope();

}
