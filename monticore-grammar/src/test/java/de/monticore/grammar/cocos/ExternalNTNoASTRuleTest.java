/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExternalNTNoASTRuleTest extends CocoTest {

  private static final String grammar = "de.monticore.grammar.cocos.invalid.A4118.A4118";
  private static final String MESSAGE = " The external production A must not have a corresponding ASTRule.";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ExternalNTNoASTRule());
  }

  @Test
  public void testValidModel(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Component", checker);
  }

  @Test
  public void testInvalidModel(){
    testInvalidGrammar(grammar, ExternalNTNoASTRule.ERROR_CODE, MESSAGE, checker);
  }
}
