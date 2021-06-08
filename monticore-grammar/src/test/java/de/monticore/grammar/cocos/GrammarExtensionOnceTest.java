/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.se_rwth.commons.logging.Log.getFindings;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;

public class GrammarExtensionOnceTest extends CocoTest {

  private final String MESSAGE = GrammarExtensionOnce.ERROR_MSG_FORMAT;
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4150.A4150";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new GrammarExtensionOnce());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, GrammarExtensionOnce.ERROR_CODE, MESSAGE, checker);
    getFindings().clear();
  }

  @Test
  public void testCorrect() {
    // Any grammar that extends another grammar
    testValidGrammar("de.monticore.grammar.cocos.valid.SubGrammarWithSymbol", checker);
  }
}
