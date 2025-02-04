/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.grammar.cocos.LexNTsNotEmpty.ERROR_CODE;


public class LexNTsNotEmptyTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not allow the empty token.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4015.A4015";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new LexNTsNotEmpty());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("de.monticore.common.TestLexicals", checker);
  }

  @Test
  public void testCorrect3() {
    testValidGrammar("de.monticore.common.TestLiterals", checker);
  }
}
