/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SplitRuleInvalidTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4062.A4062";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new SplitRuleInvalid());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar+"a", SplitRuleInvalid.ERROR_CODE,
            String.format(SplitRuleInvalid.ERROR_MSG_FORMAT, "b-"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"b", SplitRuleInvalid.ERROR_CODE,
            String.format(SplitRuleInvalid.ERROR_MSG_FORMAT, "-"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
