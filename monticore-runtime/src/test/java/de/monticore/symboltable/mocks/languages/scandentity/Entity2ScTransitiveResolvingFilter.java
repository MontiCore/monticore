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

package de.monticore.symboltable.mocks.languages.scandentity;

import static com.google.common.base.Preconditions.checkArgument;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

public class Entity2ScTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<StateChartSymbol> {

  public Entity2ScTransitiveResolvingFilter() {
    super(EntitySymbol.KIND, StateChartSymbol.class, StateChartSymbol.KIND);
  }

  @Override
  protected StateChartSymbol createAdapter(Symbol adaptee) {
    checkArgument(adaptee instanceof EntitySymbol);

    return new Entity2ScAdapter((EntitySymbol) adaptee);
  }
}
