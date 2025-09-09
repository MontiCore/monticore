/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LexProdModeNameUpperCaseTest extends CocoTest{
  private final String MESSAGE = " The lexical production %s must use Upper-case mode names.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4038.A4038";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new LexProdModeNameUpperCase());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, LexProdModeNameUpperCase.ERROR_CODE,
                         String.format(LexProdModeNameUpperCase.ERROR_MSG_FORMAT,
                              "EndTag"), checker);
  }



}
