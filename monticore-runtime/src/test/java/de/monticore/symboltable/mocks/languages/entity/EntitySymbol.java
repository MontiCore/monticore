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

package de.monticore.symboltable.mocks.languages.entity;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.CommonJTypeSymbol;

public class EntitySymbol extends CommonJTypeSymbol<EntitySymbol, PropertySymbol, ActionSymbol, EntitySymbolReference> {
  
  public static final EntitySymbolKind KIND = EntitySymbolKind.KIND;
  
  public EntitySymbol(String name) {
    super(name, EntitySymbol.KIND, PropertySymbol.KIND, ActionSymbol.KIND);
  }

  public Optional<ActionSymbol> getAction(String actionName) {
    return getMutableSpannedScope().<ActionSymbol>resolveLocally(actionName, ActionSymbol.KIND);
  }
  
  public void addAction(ActionSymbol method) {
    getMutableSpannedScope().add(method);
  }
  
  public Collection<ActionSymbol> getActions() {
    return getMutableSpannedScope().resolveLocally(ActionSymbol.KIND);
  }
  
  public Optional<PropertySymbol> getProperty(String propertyName) {
    return getMutableSpannedScope().<PropertySymbol>resolveLocally(propertyName, PropertySymbol.KIND);
  }
  
  public void addProperty(PropertySymbol property) {
    getMutableSpannedScope().add(property);
  }
  
  public Collection<PropertySymbol> getProperties() {
    return getMutableSpannedScope().resolveLocally(PropertySymbol.KIND);
  }

  @Override
  public MutableScope getMutableSpannedScope() {
    return super.getMutableSpannedScope();
  }
}
