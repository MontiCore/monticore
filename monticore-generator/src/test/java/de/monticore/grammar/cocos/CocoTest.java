/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import static org.junit.Assert.*;

public abstract class CocoTest {
  
  protected void testValidGrammar(String grammar, Grammar_WithConceptsCoCoChecker checker) {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = globalScope
        .resolveMCGrammar(grammar)
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
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = globalScope
        .resolveMCGrammar(grammar)
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
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = globalScope
        .resolveMCGrammar(grammar)
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
