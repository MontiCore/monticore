/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class UniqueProdNameInGrammarTest extends CocoTest {

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UniqueProdNameInGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0112.A0112", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112", "Bar"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0112.A0112a", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112a", "Bar"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0112.A0112b", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112b", "Bar"), checker);
  }

  @Test
  public void testInvalid4() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0112.A0112c", UniqueProdNameInGrammar.ERROR_CODE,
        String.format(UniqueProdNameInGrammar.ERROR_MSG_FORMAT, "A0112c", "Bar"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }
}
