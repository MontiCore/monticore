/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyRuleMatchingSimpleNameTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0145.A0145";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new KeyRuleMatchingSimpleName());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, KeyRuleMatchingSimpleName.ERROR_CODE, String.format(KeyRuleMatchingSimpleName.ERROR_MSG_FORMAT, "foo&"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"a", KeyRuleMatchingSimpleName.ERROR_CODE, String.format(KeyRuleMatchingSimpleName.ERROR_MSG_FORMAT, "foo&"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
