/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LexNTsOnlyUseLexNTsTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not use" +
          " the nonterminal B because B is defined by a production of" +
          " another type than lexical. Lexical productions may only reference nonterminals" +
          " defined by lexical productions.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4017.A4017";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new LexNTsOnlyUseLexNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, LexNTsOnlyUseLexNTs.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
