/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
    AutomatonWithSTInfo1Language automatonWithSTInfo1Language;
    AutomatonWithSTInfo1ModelLoader automatonWithSTInfo1ModelLoader;
    AutomatonWithSTInfo1SymbolTableCreator automatonWithSTInfo1SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertTrue(stateSymbol instanceof StateSymbol);
//    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();

    StateSymbolSurrogate stateSymbolReference;
  }

}
