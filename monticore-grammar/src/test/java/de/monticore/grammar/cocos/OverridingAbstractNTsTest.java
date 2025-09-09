/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverridingAbstractNTsTest extends CocoTest{

  private final String MESSAGE =  " The production for the abstract nonterminal ArrayType must not be overridden\n" +
          "by a production for an %s nonterminal.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4008.A4008";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingAbstractNTs());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", OverridingAbstractNTs.ERROR_CODE,
        String.format(MESSAGE, "interface"), checker);
  }
  
  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", OverridingAbstractNTs.ERROR_CODE,
        String.format(MESSAGE, "enum"), checker);
  }
  
  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", OverridingAbstractNTs.ERROR_CODE,
        String.format(MESSAGE, "lexical"), checker);
  }
  
  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", OverridingAbstractNTs.ERROR_CODE,
        String.format(MESSAGE, "external"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
