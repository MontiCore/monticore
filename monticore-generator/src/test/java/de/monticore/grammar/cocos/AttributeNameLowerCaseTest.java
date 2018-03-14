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
public class AttributeNameLowerCaseTest extends CocoTest{

  private final String MESSAGE = " The name Cs used for the nonterminal A referenced by the production B " +
          "should start with a lower-case letter.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4005.A4005";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new AttributeNameLowerCase());
  }

  @Test
  public void testUpperCasedAttribute() {
    testInvalidGrammar(grammar, AttributeNameLowerCase.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testAttributes(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
