/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementKind;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonElementSymbol;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2Language;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2ModelLoader;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo2._symboltable.AutomatonWithSTInfo2SymbolTableCreator;

public class AutomatonWithSTInfo2Test extends GeneratorIntegrationsTest {

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
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof ScopeSpanningSymbol);
//    Collection<AutomatonElementSymbol> automatonElementSymbols = automatonSymbol.getAutomatonElements();
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo2Language automatonWithSTInfo2Language;
    AutomatonWithSTInfo2ModelLoader automatonWithSTInfo2ModelLoader;
    AutomatonWithSTInfo2ModelNameCalculator automatonWithSTInfo2ModelNameCalculator;
    AutomatonWithSTInfo2SymbolTableCreator automatonWithSTInfo2SymbolTableCreator;
  }

}
