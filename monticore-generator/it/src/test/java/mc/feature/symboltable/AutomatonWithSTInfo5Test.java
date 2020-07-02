/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutomatonWithSTInfo5Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonWithSTInfo5Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolLoader automatonSymbolLoader;
    AutomatonWithSTInfo5ModelLoader automatonWithSTInfo5ModelLoader;
    AutomatonWithSTInfo5SymbolTableCreator automatonWithSTInfo5SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolLoader stateSymbolLoader;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof IScopeSpanningSymbol);
    TransitionSymbolLoader transitionSymbolReference;

//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
//    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();
//
//    StateSymbol from = transitionSymbol.getFrom();
//    StateSymbol to = transitionSymbol.getTo();

  }

}
