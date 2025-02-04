/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymbolRuleHasNameTest extends CocoTest {

  private final String grammar = "de.monticore.grammar.cocos.invalid.A0118.A0118";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new SymbolRuleHasName());
  }

  @Test
  public void testValid() {
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolRules", checker);
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, SymbolRuleHasName.ERROR_CODE, String.format(SymbolRuleHasName.ERROR_MSG, "StringReader:<8,4>"), checker);
  }

}
