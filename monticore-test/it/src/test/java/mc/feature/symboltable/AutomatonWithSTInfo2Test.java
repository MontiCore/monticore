/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.*;
import org.junit.jupiter.api.Test;

import de.se_rwth.commons.logging.Log;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonWithSTInfo2Test extends GeneratorIntegrationsTest {
  
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    assertFalse(automatonElementSymbol instanceof IScopeSpanningSymbol);
    AutomatonWithSTInfo2Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertInstanceOf(IScopeSpanningSymbol.class, automatonSymbol);
//    Collection<AutomatonElementSymbol> automatonElementSymbols = automatonSymbol.getAutomatonElements();
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo2ScopesGenitorDelegator automatonWithSTInfo2SymbolTableCreator;
    assertTrue(Log.getFindings().isEmpty());
  }

}
