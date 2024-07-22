/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NTForASTRuleExistsTest extends CocoTest{

  private final String MESSAGE = " There must not exist an AST rule for the nonterminal A" +
          " because there exists no production defining A";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4021.A4021";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTForASTRuleExists());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, NTForASTRuleExists.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
