/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.se_rwth.commons.logging.Log.getFindings;

public class GrammarExtensionOnceTest extends CocoTest {

  private final String MESSAGE = GrammarExtensionOnce.ERROR_MSG_FORMAT;
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4150.A4150";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
