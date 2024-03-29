/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NTUniqueIgnoreCaseTest extends CocoTest {
  
  private final String MESSAGE = " The nonterminal A must not be defined by more than one production: nonterminals aren't case-sensitive.";
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2026.A2026";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTUniqueIgnoreCase());
  }
  
  @Test
  public void testInvalid() {
    Log.getFindings().clear();
    testInvalidGrammar(grammar, NTUniqueIgnoreCase.ERROR_CODE, MESSAGE, checker);
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(NTUniqueIgnoreCase.ERROR_CODE + MESSAGE, f.getMsg());
    }
  }
  
}
