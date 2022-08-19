/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class AbstractNTOnlyImplementInterfaceNTsTest extends CocoTest {
  private final String MESSAGE = String.format(
      AbstractNTOnlyImplementInterfaceNTs.ERROR_MSG_FORMAT, "B", "A");
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2106.A2106";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new AbstractNTOnlyImplementInterfaceNTs());
  }

  @Test
  public void testImplementsNormalNT() {
    testInvalidGrammar(grammar + "a", AbstractNTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE,
        checker);
  }
  
  @Test
  public void testImplementsExternalNT() {
    testInvalidGrammar(grammar + "b", AbstractNTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE,
        checker);
  }
  
  @Test
  public void testImplementsAbstractNT() {
    testInvalidGrammar(grammar + "c", AbstractNTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE,
        checker);
  }
  
  @Test
  public void testImplementsEnumNT() {
    testInvalidGrammar(grammar + "d", AbstractNTOnlyImplementInterfaceNTs.ERROR_CODE, MESSAGE,
        checker);
  }

  @Test
  public void testImplementsInterfaceNT(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ImplementInterfaceNTs", checker);
  }



}
