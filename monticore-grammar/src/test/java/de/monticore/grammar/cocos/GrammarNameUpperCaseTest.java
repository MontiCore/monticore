/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GrammarNameUpperCaseTest extends CocoTest{

  private final String MESSAGE = " The grammar's name a4033 should start with an upper-case letter.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4033.a4033";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new GrammarNameUpperCase());
  }

  @Test
  public void testLowerCasedGrammarName() {
    testInvalidGrammar(grammar, GrammarNameUpperCase.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
