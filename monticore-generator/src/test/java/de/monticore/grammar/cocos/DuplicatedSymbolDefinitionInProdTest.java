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
public class DuplicatedSymbolDefinitionInProdTest extends CocoTest {

  private final String MESSAGE = " Symbol or scope is mentioned more than once in the declaration 'A'.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4041.A4041";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new DuplicatedSymbolDefinitionInProd());
  }

  @Test
  public void testDuplicatedSymbolDefinition() {
    testInvalidGrammar(grammar, DuplicatedSymbolDefinitionInProd.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
