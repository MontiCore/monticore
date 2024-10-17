/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.grammar.cocos.InterfaceNTWithoutImplementationOnlyInComponentGrammar.ERROR_CODE;

import de.se_rwth.commons.logging.Log;

public class InterfaceNTWithoutImplementationOnlyInComponentGrammarTest extends CocoTest {

  private final String MESSAGE_A = " The interface nonterminal A must not be used without nonterminals " +
      "implementing it in a grammar not marked as a grammar component.";

  private final String MESSAGE_D = " The interface nonterminal D must not be used without nonterminals " +
      "implementing it in a grammar not marked as a grammar component.";

  private final String MESSAGE_Z = " The interface nonterminal Z must not be used without nonterminals " +
      "implementing it in a grammar not marked as a grammar component.";

  private final String grammar = "de.monticore.grammar.cocos.invalid.A0278.A0278";

  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0278.A0278b";

  private final String grammar3 = "de.monticore.grammar.cocos.invalid.A0278.A0278c";

  private final String grammar4 = "de.monticore.grammar.cocos.invalid.A0278.A0278d";

  private final String grammar5 = "de.monticore.grammar.cocos.valid.InterfaceWithoutImplementation";

  private final String grammar6 = "de.monticore.grammar.cocos.valid.InterfaceWithoutImplementation2";

  private final String grammar7 = "de.monticore.grammar.cocos.valid.InterfaceWithoutImplementation3";

  private final String grammar8 = "de.monticore.grammar.cocos.invalid.A0278.A0278e";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE,
        MESSAGE_A, checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, ERROR_CODE,
        MESSAGE_A, checker);
  }

  @Test
  public void testInvalid3() {
    testInvalidGrammar(grammar3, ERROR_CODE,
        MESSAGE_A, checker);
  }

  @Test
  public void testInvalid4() {
    testInvalidGrammar(grammar4, ERROR_CODE, MESSAGE_D, checker);
  }

  @Test
  public void testInvalid5() {
    testInvalidGrammar(grammar8, ERROR_CODE, MESSAGE_Z, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Component", checker);
  }

  @Test
  public void testCorrect2() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

  @Test
  public void testCorrect3() {
    testValidGrammar(grammar5, checker);
  }

  @Test
  public void testCorrect4() {
    testValidGrammar(grammar6, checker);
  }

  @Test
  public void testCorrect5() {
    testValidGrammar(grammar7, checker);
  }

  @AfterEach
  public void after() {
    Log.getFindings().clear();
  }

}
