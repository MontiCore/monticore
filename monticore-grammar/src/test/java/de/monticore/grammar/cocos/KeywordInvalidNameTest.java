/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.grammar.cocos.KeywordInvalidName.ERROR_CODE;

public class KeywordInvalidNameTest extends CocoTest {
  private final String MESSAGE = " The production A must not use the keyword ` without naming it.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4018.A4018";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new KeywordInvalidName());
  }

  @Test
  public void testInvalid() throws IllegalArgumentException {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }


}
