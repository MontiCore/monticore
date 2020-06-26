/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class SplitRuleInvalidTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4062.A4062";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new SplitRuleInvalid());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar+"a", SplitRuleInvalid.ERROR_CODE,
            String.format(SplitRuleInvalid.ERROR_MSG_FORMAT, "b-"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"b", SplitRuleInvalid.ERROR_CODE,
            String.format(SplitRuleInvalid.ERROR_MSG_FORMAT, "-"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
