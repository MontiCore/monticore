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
public class OverridingNTsHaveNoSuperRulesTest extends CocoTest{

  private final String MESSAGE =  " The production QualifiedName overriding a production of " +
          "a super grammar must not extend the production Name.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4001.A4001";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new OverridingNTsHaveNoSuperRules());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, OverridingNTsHaveNoSuperRules.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

}
