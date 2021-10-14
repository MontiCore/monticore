/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class InheritedModiOverwriteTest extends CocoTest{
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4069.A4069Sub";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new InheritedModiOverwrite());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, InheritedModiOverwrite.ERROR_CODE,
        String.format(InheritedModiOverwrite.ERROR_MSG_FORMAT,
            "EndTag","A4069Sub", "TEXT","EndTag", "A4069Super"), checker);
  }

}
