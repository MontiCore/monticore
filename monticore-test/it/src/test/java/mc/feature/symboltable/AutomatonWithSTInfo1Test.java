/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.*;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomatonWithSTInfo1Test extends GeneratorIntegrationsTest {
  
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
    assertInstanceOf(StateSymbol.class, stateSymbol);
//    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();

    StateSymbolSurrogate stateSymbolReference;
    assertTrue(Log.getFindings().isEmpty());
  }

}
