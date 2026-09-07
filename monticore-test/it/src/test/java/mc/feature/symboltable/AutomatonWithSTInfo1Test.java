/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.symboltable.automatonwithstinfo1.AutomatonWithSTInfo1Mill;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(AutomatonWithSTInfo1Mill.class)
public class AutomatonWithSTInfo1Test {
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
  }

}
