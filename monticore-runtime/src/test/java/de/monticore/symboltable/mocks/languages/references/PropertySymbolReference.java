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

package de.monticore.symboltable.mocks.languages.references;

import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.references.SymbolReference;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.CommonTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.se_rwth.commons.logging.Log;

public class PropertySymbolReference extends PropertySymbol implements
    SymbolReference<PropertySymbol> {

  private final TypeReference<TypeSymbol> typeReference;

  private PropertySymbol referencedSymbol;

  public PropertySymbolReference(final String simpleVariableName, final Optional<String>
      definingTypeName, final Scope definingScopeOfReference) {
    super(simpleVariableName, null);

    if (definingTypeName.isPresent()) {
      typeReference = new CommonTypeReference<>(definingTypeName.get(), CommonJTypeSymbol.KIND,
          definingScopeOfReference);
    }
    else {
      typeReference = null;
    }
  }

  @Override
  public PropertySymbol getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      final EntitySymbol entitySymbol = (EntitySymbol) typeReference.getReferencedSymbol();

      referencedSymbol = entitySymbol.getProperty(getName()).orElse(null);

      if (!isReferencedSymbolLoaded()) {
        Log.error("0xA1045 " + SymbolReference.class.getSimpleName() + " Could not load full information of '" +
            getName() + "' (Kind " + getKind() + ").");
      }
    }
    return referencedSymbol;
  }

  @Override
  public boolean existsReferencedSymbol() {
    if (isReferencedSymbolLoaded()) {
      return true;
    }
    final EntitySymbol entitySymbol = (EntitySymbol) typeReference.getReferencedSymbol();
    return entitySymbol.getProperty(getName()).isPresent();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }
}

