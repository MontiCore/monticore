/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoForbiddenGrammarNameTest extends CocoTest {

  private final String MESSAGE = " There must not exist a grammar with the name I.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4036.I";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoForbiddenGrammarName());
  }

  @Test
  public void testInvalid1(){
    testInvalidGrammar(grammar, NoForbiddenGrammarName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testValid1(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ExtendNTs",checker);
  }

}
