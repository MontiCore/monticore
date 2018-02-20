/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1Language;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1ModelLoader;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.AutomatonWithSTInfo1SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateScope;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo1._symboltable.StateSymbolReference;

public class AutomatonWithSTInfo1Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo1Language automatonWithSTInfo1Language;
    AutomatonWithSTInfo1ModelLoader automatonWithSTInfo1ModelLoader;
    AutomatonWithSTInfo1ModelNameCalculator automatonWithSTInfo1ModelNameCalculator;
    AutomatonWithSTInfo1SymbolTableCreator automatonWithSTInfo1SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateScope stateScope;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertTrue(stateSymbol instanceof StateSymbol);
//    Collection<StateSymbol> stateSymbols2 = stateSymbol.getStates();

    StateSymbolReference stateSymbolReference;
  }

}
