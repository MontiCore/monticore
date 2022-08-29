/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.grammar.cocos.UsedLexNTNotDefined.ERROR_CODE;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Log;

public class UsedLexNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not" +
          " use the nonterminal B because there exists no lexical production defining B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4016.A4016";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new UsedLexNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
