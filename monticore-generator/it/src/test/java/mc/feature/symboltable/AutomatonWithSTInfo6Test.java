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

import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo6._ast.ASTBlock;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6Language;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6ModelLoader;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateSymbolReference;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionSymbolEMPTY;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionSymbolReference;
import org.junit.Test;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.ScopeSpanningSymbol;

public class AutomatonWithSTInfo6Test extends GeneratorIntegrationsTest {

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
    AutomatonSymbolEMPTY automatonSymbolEMPTY;
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo6Language automatonwithstinfo6Language;
    AutomatonWithSTInfo6ModelLoader automatonwithstinfo6ModelLoader;
    AutomatonWithSTInfo6ModelNameCalculator automatonwithstinfo6ModelNameCalculator;
    AutomatonWithSTInfo6SymbolTableCreator automatonwithstinfo6SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol;
    StateSymbolEMPTY stateSymbolEMPTY = new StateSymbolEMPTY("A");
    assertFalse(stateSymbolEMPTY instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
    TransitionKind transitionKind;
    TransitionResolvingFilter transitionResolvingFilter;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    TransitionSymbolEMPTY transitionSymbolEMPTY = new TransitionSymbolEMPTY("A");
    assertFalse(transitionSymbolEMPTY instanceof ScopeSpanningSymbol);
    TransitionSymbolReference transitionSymbolReference;

    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();

    StateSymbol from = transitionSymbol.getFrom();
    StateSymbol to = transitionSymbol.getTo();
    
    class STCreator extends AutomatonWithSTInfo6SymbolTableCreator {
      public STCreator(ResolvingConfiguration resolvingConfig, MutableScope enclosingScope) {
        super(resolvingConfig, enclosingScope);
      }
      
      @Override
      // Compiler fails if no method for scope 'Block' is generated
      protected MutableScope create_Block(ASTBlock ast) {
        // TODO Auto-generated method stub
        return super.create_Block(ast);
      }
    }

  }

}
