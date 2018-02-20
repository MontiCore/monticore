/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonElementKind;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonElementResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonElementSymbol;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonElementSymbolReference;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonWithSTInfo4Language;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonWithSTInfo4ModelLoader;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonWithSTInfo4ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.AutomatonWithSTInfo4SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.StateSymbolReference;

public class AutomatonWithSTInfo4Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementKind automatonElementKind;
    AutomatonElementResolvingFilter automatonElementResolvingFilter;
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    assertFalse(automatonElementSymbol instanceof ScopeSpanningSymbol);
    AutomatonElementSymbolReference automatonElementSymbolReference;
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol= new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof ScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo4Language automatonWithSTInfo4Language;
    AutomatonWithSTInfo4ModelLoader automatonWithSTInfo4ModelLoader;
    AutomatonWithSTInfo4ModelNameCalculator automatonWithSTInfo4ModelNameCalculator;
    AutomatonWithSTInfo4SymbolTableCreator automatonWithSTInfo4SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertFalse(stateSymbol instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;

    // StateSymbol is no (sub-)kind of AutomatonElementSymbol, even though the State rule implements AutomatonElement
    assertFalse(StateSymbol.KIND.isKindOf(AutomatonElementSymbol.KIND));

  }

}
