/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class SymbolWithManyNamesTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0279.A0279";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
    checker.addCoCo(new SymbolWithManyNames());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, SymbolWithManyNames.ERROR_CODE,
        String.format(SymbolWithManyNames.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testInvalida() {
    testInvalidGrammar(grammar+"a", SymbolWithManyNames.ERROR_CODE,
            String.format(SymbolWithManyNames.ERROR_MSG_FORMAT, "A"), checker, 2);
  }
  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.SymbolRules", checker);
  }

}
