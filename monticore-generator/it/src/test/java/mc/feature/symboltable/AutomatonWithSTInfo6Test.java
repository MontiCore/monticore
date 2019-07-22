/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo6._ast.ASTBlock;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.*;
import org.junit.Test;

import java.util.Deque;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutomatonWithSTInfo6Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonWithSTInfo6Scope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof IScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo6Language automatonwithstinfo6Language;
    AutomatonWithSTInfo6ModelLoader automatonwithstinfo6ModelLoader;
    AutomatonWithSTInfo6SymbolTableCreator automatonwithstinfo6SymbolTableCreator;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof IScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof IScopeSpanningSymbol);
    TransitionSymbolReference transitionSymbolReference;

//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
//    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();
//
//    StateSymbol from = transitionSymbol.getFrom();
//    StateSymbol to = transitionSymbol.getTo();
    
    class STCreator extends AutomatonWithSTInfo6SymbolTableCreator {
  
      public STCreator(
          IAutomatonWithSTInfo6Scope enclosingScope) {
        super(enclosingScope);
      }
  
      public STCreator(
          Deque<? extends IAutomatonWithSTInfo6Scope> scopeStack) {
        super(scopeStack);
      }
  
      @Override
      // Compiler fails if no method for scope 'Block' is generated
      protected IAutomatonWithSTInfo6Scope create_Block(ASTBlock ast) {
        return super.create_Block(ast);
      }
    }

  }

}
