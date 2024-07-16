/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenSymbolNameTest extends CocoTest{

  private final String MESSAGE = " There must not exist a symbol production with the name A4099 in the grammar A4099Symbol.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4099.A4099Symbol";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenSymbolName());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar, NoForbiddenSymbolName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
