/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultipleASTRulesTest extends CocoTest{

  private final String MESSAGE = " There must not exist more than one AST" +
          " rule for the nonterminal A.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4020.A4020";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new MultipleASTRules());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, MultipleASTRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
