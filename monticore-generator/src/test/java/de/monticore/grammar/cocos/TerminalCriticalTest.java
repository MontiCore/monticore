/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class TerminalCriticalTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4058.A4058";

  @BeforeClass
  public static void disableFailQuick() {
    Log.init();
    Log.enableFailQuick(false);
    checker.addCoCo(new TerminalCritical());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, TerminalCritical.ERROR_CODE, String.format(TerminalCritical.ERROR_MSG_FORMAT, "123"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
