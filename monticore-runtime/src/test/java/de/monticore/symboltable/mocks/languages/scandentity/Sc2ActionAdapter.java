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

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class Sc2ActionAdapter extends ActionSymbol implements SymbolAdapter<StateChartSymbol> {

  private StateChartSymbol adaptee;
  
  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionAdapter
   */
  public Sc2ActionAdapter(StateChartSymbol sc) {
    super(sc.getName());
    
    this.adaptee = Log.errorIfNull(sc);
  }
  
  /**
   * @return adaptee
   */
  public StateChartSymbol getAdaptee() {
    return this.adaptee;
  }
}
