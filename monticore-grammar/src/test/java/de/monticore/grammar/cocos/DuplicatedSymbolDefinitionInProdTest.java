/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DuplicatedSymbolDefinitionInProdTest extends CocoTest {

  private final String MESSAGE = " Symbol or scope is mentioned more than once in the declaration 'A'.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4041.A4041";
  
  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new DuplicatedSymbolDefinitionInProd());
  }

  @Test
  public void testDuplicatedSymbolDefinition() {
    testInvalidGrammar(grammar, DuplicatedSymbolDefinitionInProd.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
