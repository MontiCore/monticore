/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KeyConstantInvalidTest extends CocoTest {

  private final String grammar = "de.monticore.grammar.cocos.invalid.A4063.A4063";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new KeyConstantInvalid());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar, KeyConstantInvalid.ERROR_CODE,
            String.format(KeyConstantInvalid.ERROR_MSG_FORMAT, "--"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
