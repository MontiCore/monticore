/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.feature.symboltable.automatonwithstinfo3.AutomatonWithSTInfo3Mill;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(AutomatonWithSTInfo3Mill.class)
public class AutomatonWithSTInfo3Test {
  
  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertFalse(automatonSymbol instanceof IScopeSpanningSymbol);
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
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolSurrogate stateSymbolSurrogate;
  }

}
