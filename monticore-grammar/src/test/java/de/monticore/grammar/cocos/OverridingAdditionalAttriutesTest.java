/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;


public class OverridingAdditionalAttriutesTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4035.A4035";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    checker.addCoCo(new OverridingAdditionalAttributes());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingAdditionalAttributes.ERROR_CODE,
        String.format(OverridingAdditionalAttributes.ERROR_MSG_FORMAT, "b", "S"),
        checker, 2);
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar+"a", OverridingAdditionalAttributes.ERROR_CODE,
            String.format(OverridingAdditionalAttributes.ERROR_MSG_FORMAT, "b", "S"),
            checker, 2);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar+"b", OverridingAdditionalAttributes.ERROR_CODE,
            String.format(OverridingAdditionalAttributes.ERROR_MSG_FORMAT, "b", "T"),
            checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolRules", checker);
  }


}
