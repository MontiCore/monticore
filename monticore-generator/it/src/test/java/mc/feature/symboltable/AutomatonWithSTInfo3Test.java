/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo3._symboltable.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class AutomatonWithSTInfo3Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertFalse(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolLoader automatonSymbolLoader;
    AutomatonWithSTInfo3Language automatonWithSTInfo3Language;
    AutomatonWithSTInfo3ModelLoader automatonWithSTInfo3ModelLoader;
    AutomatonWithSTInfo3SymbolTableCreator automatonWithSTInfo3SymbolTableCreator;
    AutomatonWithSTInfo3SymbolTableCreatorDelegator automatonWithSTInfo3SymbolTableCreatorDelegator;
    AutomatonWithSTInfo3ArtifactScope automatonWithSTInfo3ArtifactScope;
    AutomatonWithSTInfo3GlobalScope automatonWithSTInfo3GlobalScope;
    AutomatonWithSTInfo3Scope automatonWithSTInfo3Scope;
    AutomatonWithSTInfo3ScopeBuilder automatonWithSTInfo3ScopeBuilder;
    AutomatonWithSTInfo3SymTabMill automatonWithSTInfo3SymTabMill;
    IAutomatonWithSTInfo3Scope iAutomatonWithSTInfo3Scope;
    ICommonAutomatonWithSTInfo3Symbol iCommonAutomatonWithSTInfo3Symbol;
    StateSymbol stateSymbol = new StateSymbol("S");
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolLoader stateSymbolLoader;
  }

}
