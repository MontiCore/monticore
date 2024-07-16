/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReferencedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The production A must not reference the " +
          "%snonterminal B because there exists no defining production for B.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2030.A2030";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ReferencedNTNotDefined());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }
  
  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }
  
  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testInvalidE() {
    testInvalidGrammar(grammar + "e", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
