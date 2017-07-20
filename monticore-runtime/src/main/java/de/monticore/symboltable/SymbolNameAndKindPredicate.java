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

package de.monticore.symboltable;


/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class SymbolNameAndKindPredicate implements SymbolPredicate {
  
  private final String symbolName;
  private final SymbolKind symbolKind;
  
  public SymbolNameAndKindPredicate(final String symbolName, final SymbolKind symbolKind) {
    this.symbolName = symbolName;
    this.symbolKind = symbolKind;
  }
  
  /**
   * @see com.google.common.base.Predicate#apply(java.lang.Object)
   */
  @Override
  public boolean test(final Symbol symbol) {
    return (symbol != null)
        && symbol.getName().equals(symbolName)
        && symbol.getKind().isKindOf(symbolKind);
  }
  
}
