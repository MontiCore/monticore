/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenSymbolNameAddonTest extends CocoTest{

  private final String MESSAGE = " There must not exist a symbol production with the name %s in the grammar %s if there is already a symbol production A.";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenSymbolNameAddon());
  }

  @Test
  public void testInvalid1(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4121.A4121a";
    String message = String.format(MESSAGE, "AMany", "A4121a");
    testInvalidGrammar(grammar, NoForbiddenSymbolNameAddon.ERROR_CODE, message, checker);
  }

  @Test
  public void testInvalid2(){
    String grammar = "de.monticore.grammar.cocos.invalid.A4121.A4121b";
    String message = String.format(MESSAGE, "AdaptedA", "A4121b");
    testInvalidGrammar(grammar, NoForbiddenSymbolNameAddon.ERROR_CODE, message, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
