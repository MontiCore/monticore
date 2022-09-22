/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class AutomatonWithSTInfo1Test extends GeneratorIntegrationsTest {
  
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
    AutomatonWithSTInfo1Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo1ScopesGenitorDelegator automatonWithSTInfo1SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertTrue(stateSymbol instanceof StateSymbol);
//    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();

    StateSymbolSurrogate stateSymbolReference;
    assertTrue(Log.getFindings().isEmpty());
  }

}
