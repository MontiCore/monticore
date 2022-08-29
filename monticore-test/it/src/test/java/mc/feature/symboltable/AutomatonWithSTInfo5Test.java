/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class AutomatonWithSTInfo5Test extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonWithSTInfo5Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo5ScopesGenitor automatonWithSTInfo5SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolSurrogate stateSymbolSurrogate;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof IScopeSpanningSymbol);
    TransitionSymbolSurrogate transitionSymbolReference;

//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
//    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();
//
//    StateSymbol from = transitionSymbol.getFrom();
//    StateSymbol to = transitionSymbol.getTo();
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
