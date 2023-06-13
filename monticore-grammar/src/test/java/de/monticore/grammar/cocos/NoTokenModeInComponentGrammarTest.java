/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class NoTokenModeInComponentGrammarTest extends CocoTest {
  private final String MESSAGE = " The lexical production %s must not define a mode in a Component Grammar.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4068.A4068";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoTokenModeInComponentGrammar());
  }

  @Test
  public void testUpperCasedPackage() {
    testInvalidGrammar(grammar, NoTokenModeInComponentGrammar.ERROR_CODE,
        String.format(NoTokenModeInComponentGrammar.ERROR_MSG_FORMAT,
            "EndTag"), checker); //kommt EndTag instead
  }


}


