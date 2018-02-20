/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class ProdWithExtensionMustNotBeOverriddenTest extends CocoTest{

  private final String MESSAGE =  " The production ArrayType must not be overridden because there"
      + " already exist productions extending it.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4010.A4010";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ProdWithExtensionMustNotBeOverridden());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdWithExtensionMustNotBeOverridden.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding2", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("mc.grammars.literals.TestLiterals", checker);
  }

  @Test
  public void testCorrect3(){
    testValidGrammar("mc.grammars.types.TestTypes", checker);
  }

}
