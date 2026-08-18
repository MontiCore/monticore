/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.feature.symboltable.automatonwithstinfo2.AutomatonWithSTInfo2Mill;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(AutomatonWithSTInfo2Mill.class)
public class AutomatonWithSTInfo2Test {

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
  }
}
