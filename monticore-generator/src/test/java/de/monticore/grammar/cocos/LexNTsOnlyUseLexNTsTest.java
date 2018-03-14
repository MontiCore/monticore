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
public class LexNTsOnlyUseLexNTsTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not use" +
          " the nonterminal B because B is defined by a production of" +
          " another type than lexical. Lexical productions may only reference nonterminals" +
          " defined by lexical productions.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4017.A4017";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new LexNTsOnlyUseLexNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, LexNTsOnlyUseLexNTs.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
