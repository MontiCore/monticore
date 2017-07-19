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

package de.monticore.symboltable.mocks.languages.statechart;

import java.util.Optional;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class StateChartScope extends CommonScope {

  /**
   * Constructor for StateChartScope
   * @param spanningSymbol
   * @param enclosingScope
   */
  public StateChartScope(StateChartSymbol spanningSymbol, Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
    setSpanningSymbol(spanningSymbol);
  }
  
 /**
 * Constructor for StateChartScope
 */
public StateChartScope(StateChartSymbol spanningSymbol) {
  this(spanningSymbol, Optional.empty());
}


}
