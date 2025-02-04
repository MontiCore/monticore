/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ASTRuleAndNTUseSameAttrNameForDiffNTsTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4028.A4028";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ASTRuleAndNTUseSameAttrNameForDiffNTs());
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
