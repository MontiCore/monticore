/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoTokenModeInComponentGrammarTest extends CocoTest{
  private final String MESSAGE = " The lexical production %s must not define a mode in a Component Grammar.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4068.A4068";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new NoTokenModeInComponentGrammar());
  }

  @Test
  public void testUpperCasedPackage() {
    testInvalidGrammar(grammar, NoTokenModeInComponentGrammar.ERROR_CODE,
        String.format(NoTokenModeInComponentGrammar.ERROR_MSG_FORMAT,
            "StartTag"), checker);
  }



}


