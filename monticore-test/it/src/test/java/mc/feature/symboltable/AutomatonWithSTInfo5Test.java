/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.feature.symboltable.automatonwithstinfo5.AutomatonWithSTInfo5Mill;
import mc.feature.symboltable.automatonwithstinfo5._symboltable.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(AutomatonWithSTInfo5Mill.class)
public class AutomatonWithSTInfo5Test {
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonWithSTInfo5Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertInstanceOf(IScopeSpanningSymbol.class, automatonSymbol);
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
  }

}
