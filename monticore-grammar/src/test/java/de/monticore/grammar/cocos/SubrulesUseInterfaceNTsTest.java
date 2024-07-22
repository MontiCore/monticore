/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubrulesUseInterfaceNTsTest extends CocoTest {
  
  private final String MESSAGE = " The production %s must use the Component %s from interface %s.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4047.A4047";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new SubrulesUseInterfaceNTs());
  }
  
  @Test
  public void TestInvalid1() {
    testInvalidGrammar(grammar + "a", SubrulesUseInterfaceNTs.ERROR_CODE, 
        String.format(MESSAGE, "B", "c", "A"), checker);
  }
  
  @Test
  public void TestInvalid2() {
    testInvalidGrammar(grammar + "b", SubrulesUseInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, "B", "c", "A"), checker);
  }
  
  @Test
  public void TestInvalid3() {
    testInvalidGrammar(grammar + "c", SubrulesUseInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, "D", "e", "A"), checker);
  }

  @Test
  public void TestInvalid4() {
    testInvalidGrammar(grammar + "d", SubrulesUseInterfaceNTs.ERROR_CODE,
      String.format(MESSAGE, "B", "foo", "A"), checker);
  }

  @Test
  public void TestInvalid5() {
    testInvalidGrammar(grammar + "e", SubrulesUseInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, "AImpl", "d*", "A"), checker);
  }

  @Test
  public void TestInvalid6() {
    testInvalidGrammar(grammar + "f", SubrulesUseInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, "BImpl", "d*", "B"), checker);
  }

  @Test
  public void TestInvalid7() {
    testInvalidGrammar(grammar + "g", SubrulesUseInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, "AImpl", "d?", "A"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.ImplementInterfaceNTs", checker);
  }

  @Test
  public void testCorrectWithOverwriting() {
    testValidGrammar(grammar + "i", checker);
  }

}
