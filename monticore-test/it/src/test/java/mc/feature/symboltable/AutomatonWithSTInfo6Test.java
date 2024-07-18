/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class AutomatonWithSTInfo6Test extends GeneratorIntegrationsTest {
  
  @BeforeEach
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
    AutomatonWithSTInfo6Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    Assertions.assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo6ScopesGenitor automatonwithstinfo6SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("A");
    Assertions.assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolSurrogate stateSymbolSurrogate;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    Assertions.assertFalse(transitionSymbol instanceof IScopeSpanningSymbol);
    TransitionSymbolSurrogate transitionSymbolSurrogate;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
