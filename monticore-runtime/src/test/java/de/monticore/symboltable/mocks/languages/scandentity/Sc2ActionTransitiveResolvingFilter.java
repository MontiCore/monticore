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

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class Sc2ActionTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<ActionSymbol> {

  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionResolver
   */
  public Sc2ActionTransitiveResolvingFilter() {
    super(StateChartSymbol.KIND, ActionSymbol.class, ActionSymbol.KIND);
  }
  

  /**
   * @see de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter#translate(de.monticore.symboltable.Symbol)
   */
  @Override
  public ActionSymbol translate(Symbol s) {
    checkArgument(s instanceof StateChartSymbol);

    return new Sc2ActionAdapter((StateChartSymbol) s);
  }
}
