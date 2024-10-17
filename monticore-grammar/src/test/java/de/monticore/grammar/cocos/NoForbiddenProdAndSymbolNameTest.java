/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenProdAndSymbolNameTest extends CocoTest{

  private final String MESSAGE = " There must not exist a production with the name ASymbol in the grammar A4122 if " +
      "there already exists a symbol with the name A.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4122.A4122";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenProdAndSymbolName());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar, NoForbiddenProdAndSymbolName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
