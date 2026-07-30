/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public abstract class CocoTest extends AbstractMCTest {

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
    assertNotNull(grammarSymbol);
    assertTrue(grammarSymbol.getAstGrammar().isPresent());

    checker.checkAll(grammarSymbol.getAstGrammar().get());
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
    checker.checkAll(grammarSymbol.getAstGrammar().get());
    
    assertEquals(numberOfFindings, Log.getFindings().size());
    Log.getFindings().removeAll(
        MCAssertions.assertHasFindingsStartingWith(code + message));
  }
}
