/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenSymbolNameTest extends CocoTest{

  private final String MESSAGE1 = " There must not exist a symbol production with the name A4099 in the grammar A4099Symbol.";
  private final String grammar1 = "de.monticore.grammar.cocos.invalid.A4099.A4099Symbol";

  private final String MESSAGE2 = " There must not exist a symbol production with the name I in the grammar A4099.";
    private final String grammar2 = "de.monticore.grammar.cocos.invalid.A4099.A4099";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenSymbolName());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar1, NoForbiddenSymbolName.ERROR_CODE, MESSAGE1, checker);
  }

  @Test
  public void testInvalid2(){
      testInvalidGrammar(grammar2, NoForbiddenSymbolName.ERROR_CODE, MESSAGE2, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
