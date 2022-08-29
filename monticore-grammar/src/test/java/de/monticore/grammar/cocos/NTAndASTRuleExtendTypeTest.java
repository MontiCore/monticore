/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class NTAndASTRuleExtendTypeTest extends CocoTest{

  private final String MESSAGE = " The AST rule for A must not extend the type " +
          "C because the production already extends a type.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4013.A4013";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new NTAndASTRuleExtendType());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, NTAndASTRuleExtendType.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
