/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3Language;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3ModelLoader;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.AutomatonWithSTInfo3SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.StateSymbolReference;

public class AutomatonWithSTInfo3Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertFalse(automatonSymbol instanceof ScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo3Language automatonWithSTInfo3Language;
    AutomatonWithSTInfo3ModelLoader automatonWithSTInfo3ModelLoader;
    AutomatonWithSTInfo3ModelNameCalculator automatonWithSTInfo3ModelNameCalculator;
    AutomatonWithSTInfo3SymbolTableCreator automatonWithSTInfo3SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertFalse(stateSymbol instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
  }

}
