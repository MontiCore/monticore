/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;


public class AbstractNTWithoutExtensionOnlyInComponentGrammarTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0277.A0277";
  private final String grammar2 = "cocos.invalid.A0277.A0277b";
  private final String grammar3 = "cocos.invalid.A0277.A0277c";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new AbstractNTWithoutExtensionOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
        String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"),
        checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
            String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
            String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Component", checker);
  }


}
