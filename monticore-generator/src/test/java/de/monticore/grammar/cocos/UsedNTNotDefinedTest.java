/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class UsedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE =" The production A must not use the nonterminal " +
          "B because there exists no production defining B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2031.A2031";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new UsedNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, UsedNTNotDefined.ERROR_CODE, MESSAGE, checker);
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    boolean found = false;
    for (Finding f : Log.getFindings()) {
      found |= f.getMsg().equals(UsedNTNotDefined.ERROR_CODE + MESSAGE);
    }
    assertTrue(found);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
