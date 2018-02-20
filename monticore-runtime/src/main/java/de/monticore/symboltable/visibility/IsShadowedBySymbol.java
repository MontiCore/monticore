/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.visibility;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class IsShadowedBySymbol implements SymbolPredicate {
  
  private final Symbol shadowedSymbol;
  
  public IsShadowedBySymbol(final Symbol shadowedSymbol) {
    this.shadowedSymbol = shadowedSymbol;
  }
  
  /**
   * @return true if the <code>shadowingSymbol</code> hides the <code>shadowedSymbol</code>.
   */
  @Override
  public boolean test(final Symbol shadowingSymbol) {
    return new ShadowsSymbol(shadowingSymbol).test(shadowedSymbol);
  }
  
}
