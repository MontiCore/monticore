/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class DerivedAndManualListNameTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2008.A2008";
  
  @BeforeEach
  public void disableFailQuick() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new DerivedAndManualListName());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar +"a", DerivedAndManualListName.ERROR_CODE,
        String.format(DerivedAndManualListName.ERROR_MSG_FORMAT, "B", "as"), checker);
  }


  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar +"b", DerivedAndManualListName.ERROR_CODE,
        String.format(DerivedAndManualListName.ERROR_MSG_FORMAT, "B", "as"), checker);
  }

  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar +"c", DerivedAndManualListName.ERROR_CODE,
        String.format(DerivedAndManualListName.ERROR_MSG_FORMAT, "B", "names"), checker);
  }

  @Test
  public void testCorrectAttributes(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrectASTRules(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ASTRules", checker);
  }

}
