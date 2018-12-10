/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

import static de.monticore.grammar.cocos.UsedLexNTNotDefined.ERROR_CODE;
import static de.se_rwth.commons.logging.Log.enableFailQuick;

public class UsedLexNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not" +
          " use the nonterminal B because there exists no lexical production defining B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4016.A4016";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new UsedLexNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
