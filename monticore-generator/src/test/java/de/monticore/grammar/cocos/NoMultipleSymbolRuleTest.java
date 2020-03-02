/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoMultipleSymbolRuleTest extends CocoTest{

  private final String MESSAGE = " A symbolRule must not exist twice for a single nonterminal. Violation by A";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4151.A4151";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NoMultipleSymbolRule());
  }

  @Test
  public void testMultipleRulesSingleNT() {
    testInvalidGrammar(grammar, NoMultipleSymbolRule.ERROR_CODE, MESSAGE, checker,2);
  }
}
