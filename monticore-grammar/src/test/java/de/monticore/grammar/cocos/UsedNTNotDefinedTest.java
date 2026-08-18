/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE =" The production A must not use the nonterminal " +
          "B because there exists no production defining B.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2031.A2031";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UsedNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, UsedNTNotDefined.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
