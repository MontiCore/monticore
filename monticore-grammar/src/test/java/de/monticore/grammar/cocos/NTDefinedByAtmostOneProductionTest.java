/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NTDefinedByAtmostOneProductionTest extends CocoTest {
  
  private final String MESSAGE = " The nonterminal A must not be defined by more than one production.";
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2025.A2025";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTDefinedByAtmostOneProduction());
  }
  
  @Test
  public void testInvalid() {
    Log.getFindings().clear();
    testInvalidGrammar(grammar, NTDefinedByAtmostOneProduction.ERROR_CODE, MESSAGE, checker, 11);
  }
  
  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }
  
}
