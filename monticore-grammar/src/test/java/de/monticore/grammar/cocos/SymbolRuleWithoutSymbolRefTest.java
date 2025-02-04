/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymbolRuleWithoutSymbolRefTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0117.A0117";
  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0117.A0117a";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new SymbolRuleWithoutSymbolRef());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, SymbolRuleWithoutSymbolRef.ERROR_CODE,
            String.format(SymbolRuleWithoutSymbolRef.ERROR_MSG_FORMAT, "B"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, SymbolRuleWithoutSymbolRef.ERROR_CODE,
            String.format(SymbolRuleWithoutSymbolRef.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
