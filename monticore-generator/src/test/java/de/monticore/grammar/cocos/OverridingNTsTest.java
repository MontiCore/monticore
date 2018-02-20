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
public class OverridingNTsTest extends CocoTest{

  private final String MESSAGE =  " The production for the nonterminal QualifiedName must not be overridden\n" +
          "by a production for an %s nonterminal.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4009.A4009";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new OverridingNTs());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", OverridingNTs.ERROR_CODE,
        String.format(MESSAGE, "interface"), checker);
  }
  
  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", OverridingNTs.ERROR_CODE, String.format(MESSAGE, "enum"),
        checker);
  }
  
  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", OverridingNTs.ERROR_CODE, String.format(MESSAGE, "lexical"),
        checker);
  }
  
  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", OverridingNTs.ERROR_CODE, String.format(MESSAGE, "external"),
        checker);
  }
  
  @Test
  public void testInvalidE() {
    testInvalidGrammar(grammar + "e", OverridingNTs.ERROR_CODE, String.format(MESSAGE, "abstract"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

}
