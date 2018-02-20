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
public class InterfaceNTWithoutImplementationOnlyInComponentGrammarTest extends CocoTest{

  private final String MESSAGE = " The interface nonterminal A must not be used without nonterminals " +
          "implementing it in a grammar not marked as a grammar component.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0278.A0278";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, InterfaceNTWithoutImplementationOnlyInComponentGrammar.ERROR_CODE,
        MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Component", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }


}
