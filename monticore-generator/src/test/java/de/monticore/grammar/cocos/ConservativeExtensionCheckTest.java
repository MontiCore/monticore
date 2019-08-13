/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConservativeExtensionCheckTest extends CocoTest  {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2007.A2007Sup";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ConservativeExtensionCheck());
  }

  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar +"1", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "M", "Name"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar +"2", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "M", "Name"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar +"3", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "M", "Name"), checker);
  }

  @Test
  public void testInvalid4() {
    testInvalidGrammar(grammar +"4", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "M", "Name"), checker);
  }

  @Test
  public void testInvalid5() {
    testInvalidGrammar(grammar +"5", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "N", "N", "M"), checker);
  }

  @Test
  public void testInvalid6() {
    testInvalidGrammar(grammar +"6", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "M", "Name"), checker);
  }

  @Test
  public void testInvalid7() {
    testInvalidGrammar(grammar +"7", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "M", "Name"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.ConservativeExtensionSup", checker);
  }

}
