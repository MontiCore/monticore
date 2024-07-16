/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverridingAbstractNTsHaveNoSuperRulesTest extends CocoTest{

  private final String MESSAGE =  " The abstract production ArrayType overriding a production of " +
          "a sub grammar must not extend the production Name.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4002.A4002";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingAbstractNTsHaveNoSuperRules());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingAbstractNTsHaveNoSuperRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
