/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author MB
 */
public class SymbolWithoutNameTest extends CocoTest {

  private final String MESSAGE = " Ensure that the symbol A contains a 'Name'.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4058.A4058";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new SymbolWithoutName());
  }

  @Test
  public void testSymbolWithoutName() {
    testInvalidGrammar(grammar + "a", SymbolWithoutName.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testSymbolWithWrongCardinalityOfName() {
    testInvalidGrammar(grammar + "b", SymbolWithoutName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testSymbolWithWrongNameReference() {
    testInvalidGrammar(grammar + "c", SymbolWithoutName.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
