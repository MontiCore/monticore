/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class AutomatonWithSTInfo2Test extends GeneratorIntegrationsTest {
  
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
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    Assertions.assertFalse(automatonElementSymbol instanceof IScopeSpanningSymbol);
    AutomatonWithSTInfo2Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    Assertions.assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
//    Collection<AutomatonElementSymbol> automatonElementSymbols = automatonSymbol.getAutomatonElements();
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo2ScopesGenitorDelegator automatonWithSTInfo2SymbolTableCreator;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
