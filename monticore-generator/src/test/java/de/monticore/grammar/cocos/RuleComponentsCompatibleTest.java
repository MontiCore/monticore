/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class RuleComponentsCompatibleTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4090.A4090";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new RuleComponentsCompatible());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testInvalidE() {
    testInvalidGrammar(grammar + "e", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testInvalidF() {
    testInvalidGrammar(grammar + "f", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "Name", "A"), checker);
  }

  @Test
  public void testInvalidG() {
    testInvalidGrammar(grammar + "g", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "Num_Int", "A"), checker);
  }

  @Test
  public void testInvalidH() {
    testInvalidGrammar(grammar + "h", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "A", "C"), checker);
  }

  @Test
  public void testInvalidI() {
    testInvalidGrammar(grammar + "i", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "who", "A", "C"), checker);
  }

  @Test
  public void testInvalidJ() {
    testInvalidGrammar(grammar + "j", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "a", "A", "C"), checker);
  }

  @Test
  public void testInvalidK() {
    testInvalidGrammar(grammar + "k", RuleComponentsCompatible.ERROR_CODE,
        String.format(RuleComponentsCompatible.ERROR_MSG_FORMAT, "B", "a", "NonTerminal", "Terminal"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.ReferencedSymbol", checker);
  }

}
