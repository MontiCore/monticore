/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymbolWithManyNamesTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0279.A0279";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolRules", checker);
  }

}
