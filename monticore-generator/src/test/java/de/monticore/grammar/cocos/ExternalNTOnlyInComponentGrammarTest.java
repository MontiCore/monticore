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
public class ExternalNTOnlyInComponentGrammarTest extends CocoTest{

  private final String MESSAGE = " The external nonterminal A must not be used in a grammar not marked " +
          "as a grammar component.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0276.A0276";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ExternalNTOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, ExternalNTOnlyInComponentGrammar.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Component", checker);
  }


}
