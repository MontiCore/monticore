/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class AttributeNameLowerCaseTest extends CocoTest{

  private final String MESSAGE = " The name C used for the nonterminal A referenced by the production B " +
          "should start with a lower-case letter.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4005.A4005";

  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new AttributeNameLowerCase());
  }
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testUpperCasedAttribute() {
    testInvalidGrammar(grammar, AttributeNameLowerCase.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testAttributes(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
