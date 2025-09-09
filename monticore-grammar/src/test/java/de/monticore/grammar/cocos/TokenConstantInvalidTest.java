/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenConstantInvalidTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4059.A4059";
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new TokenConstantInvalid());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar+"a", TokenConstantInvalid.ERROR_CODE,
            String.format(TokenConstantInvalid.ERROR_MSG_FORMAT, "b-"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"b", TokenConstantInvalid.ERROR_CODE,
            String.format(TokenConstantInvalid.ERROR_MSG_FORMAT, "-"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
