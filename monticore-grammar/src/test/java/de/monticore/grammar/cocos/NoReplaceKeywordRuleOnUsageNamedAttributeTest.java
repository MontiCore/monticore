/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoReplaceKeywordRuleOnUsageNamedAttributeTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4161.A4161";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoReplaceKeywordRuleOnUsageNamedAttribute());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoReplaceKeywordRuleOnUsageNamedAttribute.ERROR_CODE,
        String.format(NoReplaceKeywordRuleOnUsageNamedAttribute.ERROR_MSG_FORMAT, "b"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ReplaceKeyword", checker);
  }

}
