/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverridingInterfaceNTsTest extends CocoTest{

  private final String MESSAGE =  " The production for the interface nonterminal ReturnType must not be overridden.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4007.A4007a";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingInterfaceNTs());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, OverridingInterfaceNTs.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
