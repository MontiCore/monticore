/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class KeyRuleWithoutNameTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0142.A0142";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
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
