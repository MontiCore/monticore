/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverridingNTsHaveNoSuperRulesTest extends CocoTest{

  private final String MESSAGE =  " The production QualifiedName overriding a production of " +
          "a sub grammar must not extend the production Name.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4001.A4001";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingNTsHaveNoSuperRules());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, OverridingNTsHaveNoSuperRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
