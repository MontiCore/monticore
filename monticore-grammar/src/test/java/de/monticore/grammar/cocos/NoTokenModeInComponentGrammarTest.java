/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class NoTokenModeInComponentGrammarTest extends CocoTest {
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String MESSAGE = " The lexical production %s must not define a mode in a Component Grammar.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4068.A4068";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new NoTokenModeInComponentGrammar());
  }

  @Test
  public void testUpperCasedPackage() {
    testInvalidGrammar(grammar, NoTokenModeInComponentGrammar.ERROR_CODE,
        String.format(NoTokenModeInComponentGrammar.ERROR_MSG_FORMAT,
            "EndTag"), checker); //kommt EndTag instead
  }


}


