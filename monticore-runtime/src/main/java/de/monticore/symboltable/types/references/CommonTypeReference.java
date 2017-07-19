/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.symboltable.types.references;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.TypeSymbolKind;

/**
 * Default implementation of {@link TypeReference}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonTypeReference<T extends TypeSymbol> extends CommonSymbolReference<T> implements TypeReference<T> {

  private List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();

  private int dimension = 0;

  public CommonTypeReference(String referencedSymbolName, SymbolKind referencedSymbolKind, Scope definingScopeOfReference) {
    super(referencedSymbolName, referencedSymbolKind, definingScopeOfReference);
  }

  @Override
  public List<ActualTypeArgument> getActualTypeArguments() {
    return ImmutableList.copyOf(actualTypeArguments);
  }

  @Override
  public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
    this.actualTypeArguments = new ArrayList<>(actualTypeArguments);
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  public void setDimension(int arrayDimension) {
    this.dimension = arrayDimension;
  }
}
