/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoASTExtendsForClassesTest extends CocoTest{

  private final String MESSAGE = " It is forbidden to extend the rule A with the external class Observer.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4097.A4097";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoASTExtendsForClasses());
  }

 
  @Test
  public void testInvalida() {
    testInvalidGrammar(grammar + "a", NoASTExtendsForClasses.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testInvalidb() {
    testInvalidGrammar(grammar + "b", NoASTExtendsForClasses.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
