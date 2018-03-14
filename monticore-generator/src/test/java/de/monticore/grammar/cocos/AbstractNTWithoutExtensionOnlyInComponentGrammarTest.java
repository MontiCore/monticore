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
public class AbstractNTWithoutExtensionOnlyInComponentGrammarTest extends CocoTest{

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0277.A0277";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new AbstractNTWithoutExtensionOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_CODE,
        String.format(AbstractNTWithoutExtensionOnlyInComponentGrammar.ERROR_MSG_FORMAT, "A"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Component", checker);
  }


}
