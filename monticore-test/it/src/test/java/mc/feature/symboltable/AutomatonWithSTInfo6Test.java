/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.feature.symboltable.automatonwithstinfo6.AutomatonWithSTInfo6Mill;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(AutomatonWithSTInfo6Mill.class)
public class AutomatonWithSTInfo6Test {
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonWithSTInfo6Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertInstanceOf(IScopeSpanningSymbol.class, automatonSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo6ScopesGenitor automatonwithstinfo6SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolSurrogate stateSymbolSurrogate;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof IScopeSpanningSymbol);
    TransitionSymbolSurrogate transitionSymbolSurrogate;
  }

}
