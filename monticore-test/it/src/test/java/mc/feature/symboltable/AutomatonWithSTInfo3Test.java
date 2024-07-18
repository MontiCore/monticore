/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo3.AutomatonWithSTInfo3Mill;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class AutomatonWithSTInfo3Test extends GeneratorIntegrationsTest {
  
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
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    Assertions.assertFalse(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolSurrogate automatonSymbolSurrogate;
    AutomatonWithSTInfo3ScopesGenitor automatonWithSTInfo3SymbolTableCreator;
    AutomatonWithSTInfo3ScopesGenitorDelegator automatonWithSTInfo3SymbolTableCreatorDelegator;
    AutomatonWithSTInfo3ArtifactScope automatonWithSTInfo3ArtifactScope;
    AutomatonWithSTInfo3GlobalScope automatonWithSTInfo3GlobalScope;
    AutomatonWithSTInfo3Scope automatonWithSTInfo3Scope;
    AutomatonWithSTInfo3Mill automatonWithSTInfo3SymTabMill;
    IAutomatonWithSTInfo3Scope iAutomatonWithSTInfo3Scope;
    ICommonAutomatonWithSTInfo3Symbol iCommonAutomatonWithSTInfo3Symbol;
    StateSymbol stateSymbol = new StateSymbol("S");
    Assertions.assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolSurrogate stateSymbolSurrogate;
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
