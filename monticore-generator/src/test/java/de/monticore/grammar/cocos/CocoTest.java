/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public abstract class CocoTest {
  
  protected void testValidGrammar(String grammar, Grammar_WithConceptsCoCoChecker checker) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  protected void testInvalidGrammar(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker) {
    testInvalidGrammar(grammar, code, message, checker, 1);
  }
  
  protected void testInvalidGrammar(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker, int numberOfFindings) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.handle(grammarSymbol.getAstGrammar().get());
    
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(numberOfFindings, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(code + message, f.getMsg());
    }
  }
  
  protected void testInvalidGrammarKeepFindings(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker) {
    final Scope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) globalScope
        .resolve(grammar,
            MCGrammarSymbol.KIND)
        .orElse(null);
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());
    checker.handle(grammarSymbol.getAstGrammar().get());
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(code + message, f.getMsg());
    }
  }
}
