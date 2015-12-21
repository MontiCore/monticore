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

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.CommonJTypeSymbol;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class EntitySymbol extends CommonJTypeSymbol<EntitySymbol, PropertySymbol, ActionSymbol, EntitySymbolReference> {
  
  public static final EntitySymbolKind KIND = EntitySymbolKind.KIND;
  
  public EntitySymbol(String name) {
    super(name, EntitySymbol.KIND, PropertySymbol.KIND, ActionSymbol.KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new EntityScope(this);
  }

  @Override
  public EntityScope getSpannedScope() {
    return (EntityScope) super.getSpannedScope();
  }

  public Optional<ActionSymbol> getAction(String actionName) {
    return spannedScope.<ActionSymbol>resolveLocally(actionName, ActionSymbolKind.KIND);
  }
  
  public void addAction(ActionSymbol method) {
    spannedScope.add(method);
  }
  
  public Collection<ActionSymbol> getActions() {
    return spannedScope.resolveLocally(ActionSymbol.KIND);
  }
  
  public Optional<PropertySymbol> getProperty(String propertyName) {
    return spannedScope.<PropertySymbol>resolveLocally(propertyName, PropertySymbol.KIND);
  }
  
  public void addProperty(PropertySymbol property) {
    spannedScope.add(property);
  }
  
  public Collection<PropertySymbol> getProperties() {
    return spannedScope.resolveLocally(PropertySymbol.KIND);
  }
  
}
