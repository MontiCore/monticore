/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReferenceSymbolNotNameTest extends CocoTest {

  private final String grammar = "de.monticore.grammar.cocos.invalid.A4039.A4039";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ReferenceSymbolNotName());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar , ReferenceSymbolNotName.ERROR_CODE,
        ReferenceSymbolNotName.ERROR_MSG_FORMAT, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ReferencedSymbol", checker);
  }

}
