/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.GrammarGlobalScopeTestFactory;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeywordAlternativeNameTest extends CocoTest {
  private final String MESSAGE = " The name of the constant group could't be ascertained";
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4019.A4019";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
  }
  
  @Test
  public void testKeywordAlternativeWithoutName() throws IllegalArgumentException {
    final Grammar_WithConceptsGlobalScope globalScope = GrammarGlobalScopeTestFactory.create();
    
    // test grammar symbol
    globalScope.resolveMCGrammar(grammar).orElse(null);
    
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith("0xA2345" + MESSAGE));
  }
  
  @Test
  public void testSingleKeyword() {
    checker.addCoCo(new KeywordAlternativeName());
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }
  
}
