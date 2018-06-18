/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 * 
 * @author BS
 */
public class SubrulesUseInterfaceNTsTest extends CocoTest {
  
  private final String MESSAGE = " The production %s must use the non-terminal %s from interface %s.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4047.A4047";
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
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
        String.format(MESSAGE, "B", "C", "A"), checker);
  }
  
  @Test
  public void TestInvalid3() {
    testInvalidGrammar(grammar + "c", SubrulesUseInterfaceNTs.ERROR_CODE, 
        String.format(MESSAGE, "D", "E", "A"), checker);
  }

  @Test
  public void TestInvalid4() {
    testInvalidGrammar(grammar + "d", SubrulesUseInterfaceNTs.ERROR_CODE,
      String.format(MESSAGE, "B", "Foo", "A"), checker);
  }

  @Test
  public void TestInvalid5() {
    testInvalidGrammar(grammar + "e", SubrulesUseInterfaceNTs.ERROR_CODE,
            String.format(MESSAGE, "AImpl", "ds*", "A"), checker);
  }

  @Test
  public void TestInvalid6() {
    testInvalidGrammar(grammar + "f", SubrulesUseInterfaceNTs.ERROR_CODE,
            String.format(MESSAGE, "BImpl", "ds*", "B"), checker);
  }

  @Test
  public void TestInvalid7() {
    testInvalidGrammar(grammar + "g", SubrulesUseInterfaceNTs.ERROR_CODE,
            String.format(MESSAGE, "AImpl", "D?", "A"), checker);
  }
  
  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.ImplementInterfaceNTs", checker);
  }

}
