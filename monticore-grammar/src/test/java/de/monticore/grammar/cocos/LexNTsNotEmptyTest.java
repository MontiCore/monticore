/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.grammar.cocos.LexNTsNotEmpty.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import de.se_rwth.commons.logging.Log;

public class LexNTsNotEmptyTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not allow the empty token.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4015.A4015";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new LexNTsNotEmpty());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("de.monticore.common.TestLexicals", checker);
  }

  @Test
  public void testCorrect3() {
    testValidGrammar("de.monticore.common.TestLiterals", checker);
  }
}
