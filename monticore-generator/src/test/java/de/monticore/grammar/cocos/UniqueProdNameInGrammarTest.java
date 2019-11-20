// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class UniqueProdNameInGrammarTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new UniqueProdNameInGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar("cocos.invalid.A0112.A0112", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112", "Bar"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar("cocos.invalid.A0112.A0112a", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112a", "Bar"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar("cocos.invalid.A0112.A0112b", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112b", "Bar"), checker);
  }

  @Test
  public void testInvalid4() {
    testInvalidGrammar("cocos.invalid.A0112.A0112c", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112c", "Bar"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }
}
