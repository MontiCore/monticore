/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class TerminalEmptyStringTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4054.A4054";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new TerminalEmptyString());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, TerminalEmptyString.ERROR_CODE, TerminalEmptyString.ERROR_MSG_FORMAT, checker);
  }


  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("mc.grammars.lexicals.TestLexicals", checker);
  }

  @Test
  public void testCorrect3(){
    testValidGrammar("mc.grammars.literals.TestLiterals", checker);
  }
}
