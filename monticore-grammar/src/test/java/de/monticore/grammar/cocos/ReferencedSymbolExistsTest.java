/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReferencedSymbolExistsTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4037.A4037";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ReferencedSymbolExists());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ReferencedSymbolExists.ERROR_CODE,
        String.format(ReferencedSymbolExists.ERROR_MSG_FORMAT, "C"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ReferencedSymbol", checker);
  }

}
