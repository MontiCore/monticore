/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class ProdWithExtensionMustNotBeOverriddenTest extends CocoTest{

  private final String MESSAGE =  " The production ArrayType must not be overridden because there"
      + " already exist productions extending it.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4010.A4010";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new ProdWithExtensionMustNotBeOverridden());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdWithExtensionMustNotBeOverridden.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding2", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("de.monticore.common.TestLiterals", checker);
  }

  @Test
  public void testCorrect3(){
    testValidGrammar("de.monticore.common.TestTypes", checker);
  }

}
