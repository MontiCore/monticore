/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoReplaceKeywordRuleOnConstantGroupTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4162.A4162";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void addCoCoToChecker() {
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
