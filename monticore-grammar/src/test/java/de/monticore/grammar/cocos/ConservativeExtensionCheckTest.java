/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConservativeExtensionCheckTest extends CocoTest  {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2007.A2007Sup";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ConservativeExtensionCheck());
  }
  
  @Test
  public void testInvalid1() {
    testInvalidGrammar(grammar +"1", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar +"2", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar +"3", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testInvalid4() {
    testInvalidGrammar(grammar +"4", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testInvalid5() {
    testInvalidGrammar(grammar +"5", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "N", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.N", "m"), checker);
  }

  @Test
  public void testInvalid6() {
    testInvalidGrammar(grammar +"6", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "A", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testInvalid7() {
    testInvalidGrammar(grammar +"7", ConservativeExtensionCheck.ERROR_CODE,
        String.format(ConservativeExtensionCheck.ERROR_MSG_FORMAT, "M", "de.monticore.grammar.cocos.invalid.A2007.A2007Super.M", "name"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ConservativeExtensionSup", checker);
  }

}
