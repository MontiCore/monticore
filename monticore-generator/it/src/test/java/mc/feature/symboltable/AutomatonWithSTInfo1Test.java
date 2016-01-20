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

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1Language;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1ModelLoader;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateScope;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateSymbolReference;
import org.junit.Test;

public class AutomatonWithSTInfo1Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbolEMPTY automatonSymbolEMPTY = new AutomatonSymbolEMPTY("E");
    assertTrue(automatonSymbolEMPTY instanceof ScopeSpanningSymbol);
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof AutomatonSymbolEMPTY);
    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo1Language automatonWithSTInfo1Language;
    AutomatonWithSTInfo1ModelLoader automatonWithSTInfo1ModelLoader;
    AutomatonWithSTInfo1ModelNameCalculator automatonWithSTInfo1ModelNameCalculator;
    AutomatonWithSTInfo1SymbolTableCreator automatonWithSTInfo1SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateScope stateScope;
    StateSymbolEMPTY stateSymbolEMPTY = new StateSymbolEMPTY("T");
    assertTrue(stateSymbolEMPTY instanceof ScopeSpanningSymbol);
    StateSymbol stateSymbol = new StateSymbol("S");
    assertTrue(stateSymbol instanceof StateSymbolEMPTY);
    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();
    assertFalse(stateSymbol.isFinal());
    stateSymbol.setFinal(true);
    assertTrue(stateSymbol.isFinal());
    stateSymbol.isInitial();
    stateSymbol.setInitial(true);
    StateSymbolReference stateSymbolReference;
  }

}
