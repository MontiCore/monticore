/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo4._ast.ASTState;
import mc.feature.symboltable.automatonwithstinfo4._ast.AutomatonWithSTInfo4Mill;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutomatonWithSTInfo4Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    assertFalse(automatonElementSymbol instanceof IScopeSpanningSymbol);
    AutomatonElementSymbolLoader automatonElementSymbolLoader;
    AutomatonWithSTInfo4Scope automatonScope;
    AutomatonSymbol automatonSymbol= new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolLoader automatonSymbolLoader;
    AutomatonWithSTInfo4Language automatonWithSTInfo4Language;
    AutomatonWithSTInfo4ModelLoader automatonWithSTInfo4ModelLoader;
    AutomatonWithSTInfo4SymbolTableCreator automatonWithSTInfo4SymbolTableCreator;
    ASTState s = AutomatonWithSTInfo4Mill.stateBuilder().setName("S").build();
    s.setSymbol(new AutomatonElementSymbol("S") );
    AutomatonElementSymbol aESymbol = s.getSymbol();


  }

}
