/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoReplaceKeywordRuleOnUsageNamedAttributeTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4161.A4161";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
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
