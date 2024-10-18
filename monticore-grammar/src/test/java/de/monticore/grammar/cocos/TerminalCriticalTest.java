/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TerminalCriticalTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4058.A4058";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new TerminalCritical());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, TerminalCritical.ERROR_CODE, String.format(TerminalCritical.ERROR_MSG_FORMAT, "123"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
