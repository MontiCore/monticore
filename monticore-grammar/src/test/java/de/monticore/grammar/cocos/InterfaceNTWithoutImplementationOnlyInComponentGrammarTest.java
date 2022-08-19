/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.grammar.cocos.InterfaceNTWithoutImplementationOnlyInComponentGrammar.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import de.se_rwth.commons.logging.Log;

public class InterfaceNTWithoutImplementationOnlyInComponentGrammarTest extends CocoTest {

  private final String MESSAGE = " The interface nonterminal A must not be used without nonterminals " +
          "implementing it in a grammar not marked as a grammar component.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0278.A0278";
  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0278.A0278b";
  private final String grammar3 = "de.monticore.grammar.cocos.invalid.A0278.A0278c";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE,
            MESSAGE, checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, ERROR_CODE,
        MESSAGE, checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, ERROR_CODE,
        MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Component", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }


}
