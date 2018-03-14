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
public class NTDefinedByAtmostOneProductionTest extends CocoTest {
  
  private final String MESSAGE = " The nonterminal A must not be defined by more than one production.";
  
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  
  private final String grammar = "cocos.invalid.A2025.A2025";
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new NTDefinedByAtmostOneProduction());
  }
  
  @Test
  public void testInvalid() {
    Log.getFindings().clear();
    testInvalidGrammar(grammar, NTDefinedByAtmostOneProduction.ERROR_CODE, MESSAGE, checker, 11);
  }
  
  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }
  
}
