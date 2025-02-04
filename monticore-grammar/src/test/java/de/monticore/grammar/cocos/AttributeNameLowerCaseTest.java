/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttributeNameLowerCaseTest extends CocoTest{

  private final String MESSAGE = " The name C used for the nonterminal A referenced by the production B " +
          "should start with a lower-case letter.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4005.A4005";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new AttributeNameLowerCase());
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
