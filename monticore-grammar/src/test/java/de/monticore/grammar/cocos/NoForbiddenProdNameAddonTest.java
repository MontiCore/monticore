/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenProdNameAddonTest extends CocoTest{

  private final String MESSAGE = " There must not exist a production with the name ABuilder in the grammar A4120 if there is already a production with the name A.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4120.A4120";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenProdNameAddon());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar, NoForbiddenProdNameAddon.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
