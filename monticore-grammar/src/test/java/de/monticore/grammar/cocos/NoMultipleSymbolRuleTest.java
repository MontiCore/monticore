/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoMultipleSymbolRuleTest extends CocoTest{

  private final String MESSAGE = " A symbolRule must not exist twice for a single nonterminal. Violation by A";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4151.A4151";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoMultipleSymbolRule());
  }

  @Test
  public void testMultipleRulesSingleNT() {
    testInvalidGrammar(grammar, NoMultipleSymbolRule.ERROR_CODE, MESSAGE, checker,2);
  }
}
