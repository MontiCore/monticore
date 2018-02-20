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
public class ProdAndExtendedProdUseSameAttrNameForDiffNTsTest extends CocoTest{

  private final String MESSAGE = " The production B extending the production A must not use the\n" +
          "name a for the nonterminal D as A already uses this name for the nonterminal C.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4024.A4024";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ProdAndExtendedProdUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdAndExtendedProdUseSameAttrNameForDiffNTs.ERROR_CODE, MESSAGE,
        checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("mc.grammars.types.TestTypes", checker);
  }

}
