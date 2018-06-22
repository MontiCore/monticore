package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReferencedSymbolExistsTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4037.A4037";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ReferencedSymbolExists());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ReferencedSymbolExists.ERROR_CODE,
        String.format(ReferencedSymbolExists.ERROR_MSG_FORMAT, "C"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.ReferencedSymbol", checker);
  }

}
