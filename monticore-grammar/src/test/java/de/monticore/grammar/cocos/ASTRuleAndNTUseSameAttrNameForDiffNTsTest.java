/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class ASTRuleAndNTUseSameAttrNameForDiffNTsTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4028.A4028";

  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new ASTRuleAndNTUseSameAttrNameForDiffNTs());
  }
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ASTRuleAndNTUseSameAttrNameForDiffNTs.ERROR_CODE,
        String.format(ASTRuleAndNTUseSameAttrNameForDiffNTs.ERROR_MSG_FORMAT, "E", "a", "B", "A"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
