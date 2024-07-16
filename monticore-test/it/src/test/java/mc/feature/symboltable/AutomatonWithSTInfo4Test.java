/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo4.AutomatonWithSTInfo4Mill;
import mc.feature.symboltable.automatonwithstinfo4._ast.ASTState;
import mc.feature.symboltable.automatonwithstinfo4._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class AutomatonWithSTInfo4Test extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonElementSymbol automatonElementSymbol = new AutomatonElementSymbol("A");
    Assertions.assertFalse(automatonElementSymbol instanceof IScopeSpanningSymbol);
    AutomatonElementSymbolSurrogate automatonElementSymbolSurrogate;
    AutomatonWithSTInfo4Scope automatonScope;
    AutomatonSymbol automatonSymbol= new AutomatonSymbol("A");
    Assertions.assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo4ScopesGenitor automatonWithSTInfo4SymbolTableCreator;
    ASTState s = AutomatonWithSTInfo4Mill.stateBuilder().setName("S").build();
    s.setSymbol(new AutomatonElementSymbol("S") );
    AutomatonElementSymbol aESymbol = s.getSymbol();
  
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
