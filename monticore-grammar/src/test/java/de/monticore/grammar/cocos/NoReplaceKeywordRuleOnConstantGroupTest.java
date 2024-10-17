/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoReplaceKeywordRuleOnConstantGroupTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4162.A4162";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoReplaceKeywordRuleOnConstantGroup());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoReplaceKeywordRuleOnConstantGroup.ERROR_CODE,
        String.format(NoReplaceKeywordRuleOnConstantGroup.ERROR_MSG_FORMAT, "cg"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ReplaceKeyword", checker);
  }

}
