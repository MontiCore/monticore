/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoASTRuleForEnumNTsTest extends CocoTest{

  private final String MESSAGE = " There must not exist an AST rule for the enum nonterminal A.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4032.A4032";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoASTRuleForEnumNTs());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, NoASTRuleForEnumNTs.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
