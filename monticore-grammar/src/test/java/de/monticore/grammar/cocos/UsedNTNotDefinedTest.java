/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE =" The production A must not use the nonterminal " +
          "B because there exists no production defining B.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A2031.A2031";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UsedNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, UsedNTNotDefined.ERROR_CODE, MESSAGE, checker);
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(1, Log.getFindings().size());
    boolean found = false;
    for (Finding f : Log.getFindings()) {
      found |= f.getMsg().equals(UsedNTNotDefined.ERROR_CODE + MESSAGE);
    }
    Assertions.assertTrue(found);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
