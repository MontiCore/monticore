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

import org.junit.Test;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementKind;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementSymbol;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2Language;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2ModelLoader;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2SymbolTableCreator;

public class AutomatonWithSTInfo2Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementKind automatonElementKind;
    AutomatonElementResolvingFilter automatonElementResolvingFilter;
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    assertFalse(automatonElementSymbol instanceof ScopeSpanningSymbol);
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof ScopeSpanningSymbol);
    Collection<AutomatonElementSymbol> automatonElementSymbols = automatonSymbol.getAutomatonElements();
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo2Language automatonWithSTInfo2Language;
    AutomatonWithSTInfo2ModelLoader automatonWithSTInfo2ModelLoader;
    AutomatonWithSTInfo2ModelNameCalculator automatonWithSTInfo2ModelNameCalculator;
    AutomatonWithSTInfo2SymbolTableCreator automatonWithSTInfo2SymbolTableCreator;
  }

}
