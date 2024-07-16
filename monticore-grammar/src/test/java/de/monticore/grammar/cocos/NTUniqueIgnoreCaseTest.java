/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NTUniqueIgnoreCaseTest extends CocoTest {
  
  private final String MESSAGE = " The nonterminal A must not be defined by more than one production: nonterminals aren't case-sensitive.";
  
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2026.A2026";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NTUniqueIgnoreCase());
  }
  
  @Test
  public void testInvalid() {
    Log.getFindings().clear();
    testInvalidGrammar(grammar, NTUniqueIgnoreCase.ERROR_CODE, MESSAGE, checker);
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      Assertions.assertEquals(NTUniqueIgnoreCase.ERROR_CODE + MESSAGE, f.getMsg());
    }
  }
  
}
