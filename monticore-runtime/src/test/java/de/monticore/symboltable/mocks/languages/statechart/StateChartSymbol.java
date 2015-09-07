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

package de.monticore.symboltable.mocks.languages.statechart;

import java.util.Optional;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.MutableScope;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class StateChartSymbol extends CommonScopeSpanningSymbol {

  public static final StateChartKind KIND = StateChartKind.KIND;
  
  /**
   * @param name
   */
  public StateChartSymbol(String name) {
    super(name, StateChartKind.KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new StateChartScope(this);
  }

  public Optional<StateSymbol> getState(String simpleName) {
    return spannedScope.<StateSymbol>resolveLocally(simpleName, StateKind.KIND);
  }
  
  public void addState(StateSymbol state) {
    spannedScope.define(state);
  }

}
