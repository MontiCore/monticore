/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyRuleWithoutNameTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0142.A0142";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    LogStub.enableFailQuick(false);
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
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
