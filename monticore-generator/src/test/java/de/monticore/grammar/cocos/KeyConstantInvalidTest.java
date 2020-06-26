/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyConstantInvalidTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4063.A4063";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new KeyConstantInvalid());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar, KeyConstantInvalid.ERROR_CODE,
            String.format(KeyConstantInvalid.ERROR_MSG_FORMAT, "--"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
