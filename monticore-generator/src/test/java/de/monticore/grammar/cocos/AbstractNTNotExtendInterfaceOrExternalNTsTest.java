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
public class AbstractNTNotExtendInterfaceOrExternalNTsTest extends CocoTest {
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  private final String grammar = "cocos.invalid.A2107.A2107";
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new AbstractNTNotExtendInterfaceOrExternalNTs());
  }
  
  @Test
  public void testExtendInterfaceNT() {
    testInvalidGrammar(grammar + "a", AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_MSG_FORMAT, "B", "interface",
            "A"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_CODE,
        String.format(AbstractNTNotExtendInterfaceOrExternalNTs.ERROR_MSG_FORMAT, "B", "external",
            "A"), checker);
  }
  
  @Test
  public void testExtendNT() {
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
