/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.ScopeSpanningSymbol;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.automatonwithstinfo6._ast.ASTBlock;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonScope;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonSymbolReference;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6Language;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6ModelLoader;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6ModelNameCalculator;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.AutomatonWithSTInfo6SymbolTableCreator;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.StateSymbolReference;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionKind;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionResolvingFilter;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionSymbol;
import mc.feature.symboltable.automatonwithstinfo6._symboltable.TransitionSymbolReference;

public class AutomatonWithSTInfo6Test extends GeneratorIntegrationsTest {

  /**
   * This test ensures that all expected classes are generated. Otherwise, the test will not compile
   */
  @SuppressWarnings("unused")
  @Test
  public void test() {
    AutomatonKind automatonKind;
    AutomatonResolvingFilter automatonResolvingFilter;
    AutomatonScope automatonScope;
    AutomatonSymbol automatonSymbol = new AutomatonSymbol("A");
    assertTrue(automatonSymbol instanceof ScopeSpanningSymbol);
    AutomatonSymbolReference automatonSymbolReference;
    AutomatonWithSTInfo6Language automatonwithstinfo6Language;
    AutomatonWithSTInfo6ModelLoader automatonwithstinfo6ModelLoader;
    AutomatonWithSTInfo6ModelNameCalculator automatonwithstinfo6ModelNameCalculator;
    AutomatonWithSTInfo6SymbolTableCreator automatonwithstinfo6SymbolTableCreator;
    StateKind stateKind;
    StateResolvingFilter stateResolvingFilter;
    StateSymbol stateSymbol = new StateSymbol("A");
    assertFalse(stateSymbol instanceof ScopeSpanningSymbol);
    StateSymbolReference stateSymbolReference;
    TransitionKind transitionKind;
    TransitionResolvingFilter transitionResolvingFilter;
    TransitionSymbol transitionSymbol = new TransitionSymbol("T");
    assertFalse(transitionSymbol instanceof ScopeSpanningSymbol);
    TransitionSymbolReference transitionSymbolReference;

//    Collection<StateSymbol> stateSymbols = automatonSymbol.getStates();
//    Collection<TransitionSymbol> transitionSymbols = automatonSymbol.getTransitions();
//
//    StateSymbol from = transitionSymbol.getFrom();
//    StateSymbol to = transitionSymbol.getTo();
    
    class STCreator extends AutomatonWithSTInfo6SymbolTableCreator {
      public STCreator(ResolvingConfiguration resolvingConfig, MutableScope enclosingScope) {
        super(resolvingConfig, enclosingScope);
      }
      
      @Override
      // Compiler fails if no method for scope 'Block' is generated
      protected MutableScope create_Block(ASTBlock ast) {
        return super.create_Block(ast);
      }
    }

  }

}
