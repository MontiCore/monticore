/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractNTWithoutExtensionOnlyInComponentGrammarTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0277.A0277";
  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0277.A0277b";
  private final String grammar3 = "de.monticore.grammar.cocos.invalid.A0277.A0277c";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new AbstractNTWithoutExtensionOnlyInComponentGrammar());
  }
  
  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
        String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"),
        checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
            String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
            String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Component", checker);
  }


}
