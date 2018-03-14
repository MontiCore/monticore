/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by
 *
 * @author KH
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTsTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4028.A4028";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
    checker.addCoCo(new ASTRuleAndNTUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ASTRuleAndNTUseSameAttrNameForDiffNTs.ERROR_CODE,
        String.format(ASTRuleAndNTUseSameAttrNameForDiffNTs.ERROR_MSG_FORMAT, "E", "a", "B", "B", "A"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.ASTRules", checker);
  }

}
