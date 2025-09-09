/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NTAndASTRuleExtendTypeTest extends CocoTest{

  private final String MESSAGE = " The AST rule for A must not extend the type " +
          "C because the production already extends a type.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4013.A4013";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTAndASTRuleExtendType());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, NTAndASTRuleExtendType.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
