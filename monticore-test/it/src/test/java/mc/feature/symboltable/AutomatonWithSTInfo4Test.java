/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.feature.symboltable.automatonwithstinfo4.AutomatonWithSTInfo4Mill;
import mc.feature.symboltable.automatonwithstinfo4._ast.ASTState;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(AutomatonWithSTInfo4Mill.class)
public class AutomatonWithSTInfo4Test {
  
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    assertFalse(automatonElementSymbol instanceof IScopeSpanningSymbol);
    AutomatonElementSymbolSurrogate automatonElementSymbolSurrogate;
    AutomatonWithSTInfo4Scope automatonScope;
    AutomatonSymbol automatonSymbol= new AutomatonSymbol("A");
    assertInstanceOf(IScopeSpanningSymbol.class, automatonSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo4ScopesGenitor automatonWithSTInfo4SymbolTableCreator;
    ASTState s = AutomatonWithSTInfo4Mill.stateBuilder().setName("S").build();
    s.setSymbol(new AutomatonElementSymbol("S") );
    AutomatonElementSymbol aESymbol = s.getSymbol();
  }

}
