/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.grammar.cocos.UsedLexNTNotDefined.ERROR_CODE;

public class UsedLexNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not" +
          " use the nonterminal B because there exists no lexical production defining B.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4016.A4016";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new UsedLexNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
