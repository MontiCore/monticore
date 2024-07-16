/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyRuleWithoutNameTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0142.A0142";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new KeyRuleWithoutName());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, KeyRuleWithoutName.ERROR_CODE, KeyRuleWithoutName.ERROR_MSG_FORMAT, checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"a", KeyRuleWithoutName.ERROR_CODE, KeyRuleWithoutName.ERROR_MSG_FORMAT, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
