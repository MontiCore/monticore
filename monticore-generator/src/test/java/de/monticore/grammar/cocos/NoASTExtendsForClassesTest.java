/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoASTExtendsForClassesTest extends CocoTest{

  private final String MESSAGE = " It is not allowed to extend the rule A with the external class java.util.Observer.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4097.A4097";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
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
    testValidGrammar("cocos.valid.ASTRules", checker);
  }

}
