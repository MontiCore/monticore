/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
 */
public abstract class CommonScopeSpanningSymbol extends CommonSymbol implements ScopeSpanningSymbol {

  protected final MutableScope spannedScope;

  /**
   * @see CommonSymbol#CommonSymbol(String, SymbolKind)
   */
  public CommonScopeSpanningSymbol(String name, SymbolKind kind) {
    super(name, kind);

    spannedScope = createSpannedScope();
    getMutableSpannedScope().setSpanningSymbol(this);
  }

  /**
   * Factory method for creating the scope spanned by this symbol. By default, a
   * {@link CommonScope} is spanned.
   *
   * @return the (newly) created scope spanned by this symbol.
   */
  protected MutableScope createSpannedScope() {
    return new CommonScope(true);
  }

  @Override
  public Scope getSpannedScope() {
    return getMutableSpannedScope();
  }

  protected MutableScope getMutableSpannedScope() {
    return spannedScope;
  }


  @Override
  public void setEnclosingScope(MutableScope scope) {
    super.setEnclosingScope(scope);
   getMutableSpannedScope().setEnclosingScope(scope);
  }

}
