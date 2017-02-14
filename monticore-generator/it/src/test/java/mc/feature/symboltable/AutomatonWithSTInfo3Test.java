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

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3Language;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3ModelLoader;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateSymbolReference;
import org.junit.Test;

public class AutomatonWithSTInfo3Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonSymbol automatonSymbol;
    AutomatonSymbolEMPTY automatonSymbolEMPTY = new AutomatonSymbolEMPTY("A");
    assertFalse(automatonSymbolEMPTY instanceof ScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo3Language automatonWithSTInfo3Language;
    AutomatonWithSTInfo3ModelLoader automatonWithSTInfo3ModelLoader;
    AutomatonWithSTInfo3ModelNameCalculator automatonWithSTInfo3ModelNameCalculator;
    AutomatonWithSTInfo3SymbolTableCreator automatonWithSTInfo3SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol;
    StateSymbolEMPTY stateSymbolEMPTY = new StateSymbolEMPTY("S");
    assertFalse(stateSymbolEMPTY instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
  }

}
