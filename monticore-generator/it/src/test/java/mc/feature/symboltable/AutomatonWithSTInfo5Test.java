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

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonWithSTInfo5Language;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonWithSTInfo5ModelLoader;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonWithSTInfo5ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.AutomatonWithSTInfo5SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.StateSymbolReference;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.TransitionKind;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.TransitionResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.TransitionSymbol;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.TransitionSymbolReference;

public class AutomatonWithSTInfo5Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof ScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo5Language automatonWithSTInfo5Language;
    AutomatonWithSTInfo5ModelLoader automatonWithSTInfo5ModelLoader;
    AutomatonWithSTInfo5ModelNameCalculator automatonWithSTInfo5ModelNameCalculator;
    AutomatonWithSTInfo5SymbolTableCreator automatonWithSTInfo5SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
    TransitionKind transitionKind;
    TransitionResolvingFilter transitionResolvingFilter;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof ScopeSpanningSymbol);
    TransitionSymbolReference transitionSymbolReference;

//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
//    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();
//
//    StateSymbol from = transitionSymbol.getFrom();
//    StateSymbol to = transitionSymbol.getTo();

  }

}
