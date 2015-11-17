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

package de.monticore.symboltable.mocks.languages.entity;

import java.util.Optional;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.CommonJMethodSymbol;

public class ActionSymbol extends CommonJMethodSymbol<EntitySymbol, EntitySymbolReference, PropertySymbol> {
  
  public static final ActionSymbolKind KIND = ActionSymbolKind.KIND;
  
  public ActionSymbol(String name) {
    super(name, ActionSymbolKind.KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new ActionScope(this);
  }

  public Optional<PropertySymbol> getVariable(String name) {
    return spannedScope.<PropertySymbol>resolveLocally(name, PropertySymbolKind.KIND);
  }
  
  public void addVariable(PropertySymbol variable) {
    spannedScope.add(variable);
  }

  
  @Override
  public String toString() {
    return "method " + super.toString() + ":" + spannedScope.getSymbols();
  }

}
