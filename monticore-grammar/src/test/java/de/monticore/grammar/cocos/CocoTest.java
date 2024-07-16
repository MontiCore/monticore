/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public abstract class CocoTest {

  protected Grammar_WithConceptsCoCoChecker checker;

  @BeforeEach
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }
  
  protected void testValidGrammar(String grammar, Grammar_WithConceptsCoCoChecker checker) {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = globalScope
        .resolveMCGrammar(grammar)
        .orElse(null);
    Assertions.assertNotNull(grammarSymbol);
    Assertions.assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.checkAll(grammarSymbol.getAstGrammar().get());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertNotNull(grammarSymbol);
    Assertions.assertTrue(grammarSymbol.getAstGrammar().isPresent());
    
    Log.getFindings().clear();
    checker.checkAll(grammarSymbol.getAstGrammar().get());
    
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(numberOfFindings, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      Assertions.assertEquals(code + message, f.getMsg());
    }
  }
  
  protected void testInvalidGrammarKeepFindings(String grammar, String code, String message,
      Grammar_WithConceptsCoCoChecker checker) {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = globalScope
        .resolveMCGrammar(grammar)
        .orElse(null);
    Assertions.assertNotNull(grammarSymbol);
    Assertions.assertTrue(grammarSymbol.getAstGrammar().isPresent());
    checker.checkAll(grammarSymbol.getAstGrammar().get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      Assertions.assertEquals(code + message, f.getMsg());
    }
  }
}
