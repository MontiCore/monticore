/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.visibility;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Scopes;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ShadowsSymbol implements SymbolPredicate {
  
  private final Symbol shadowingSymbol;

  public ShadowsSymbol(Symbol shadowingSymbol) {
    this.shadowingSymbol = Log.errorIfNull(shadowingSymbol);
  }

  @Override
  public boolean test(Symbol symbol) {
    Log.errorIfNull(symbol);
    
    final Scope shadowingScope = shadowingSymbol.getEnclosingScope();
    final Scope shadowedScope = symbol.getEnclosingScope();
    
    if (shadowingScope == shadowedScope) {
      // Something to do here?
    }
    else if (Scopes.getFirstShadowingScope(shadowingScope).isPresent()) {
      final Scope firstShadowingScope = Scopes.getFirstShadowingScope(shadowingScope).get();
        
      if (Scopes.isDescendant(firstShadowingScope, shadowedScope)) {
        return shadowingSymbol.isKindOf(symbol.getKind()) && shadowingSymbol.getName().equals(symbol.getName());
      }
    }
    
    return false;
    
  }
  
}
