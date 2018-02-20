/* (c) https://github.com/MontiCore/monticore */

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
