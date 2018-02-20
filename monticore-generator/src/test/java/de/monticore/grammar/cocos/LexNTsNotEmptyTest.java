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
public class LexNTsNotEmptyTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not allow the empty token.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4015.A4015";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new LexNTsNotEmpty());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, LexNTsNotEmpty.ERROR_CODE, MESSAGE, checker);
  }
  
  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }
  
  @Test
  public void testCorrect2() {
    testValidGrammar("mc.grammars.lexicals.TestLexicals", checker);
  }

  @Test
  public void testCorrect3(){
    testValidGrammar("mc.grammars.literals.TestLiterals", checker);
  }
}
