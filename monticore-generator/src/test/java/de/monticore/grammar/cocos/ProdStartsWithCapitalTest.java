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
public class ProdStartsWithCapitalTest extends CocoTest {

  private final String MESSAGE = " The nonterminal a should not start with a lower-case letter.";;
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4031.A4031";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ProdStartsWithCapital());
  }

  @Test
  public void testNT() {
    testInvalidGrammar(grammar + "a", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testAbstractNT() {
    testInvalidGrammar(grammar + "b", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testInterfaceNT() {
    testInvalidGrammar(grammar + "c", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testExternalNT() {
    testInvalidGrammar(grammar + "d", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testEnumNT() {
    testInvalidGrammar(grammar + "e", ProdStartsWithCapital.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testExtendInterfaceNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
