/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class AutomatonWithSTInfo1Test extends GeneratorIntegrationsTest {
  
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
    AutomatonWithSTInfo1Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo1ScopesGenitorDelegator automatonWithSTInfo1SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("S");
    Assertions.assertTrue(stateSymbol instanceof StateSymbol);
//    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();

    StateSymbolSurrogate stateSymbolReference;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
