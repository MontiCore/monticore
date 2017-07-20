/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
