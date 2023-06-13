/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class InheritedModiOverwriteTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4069.A4069Sub";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new InheritedModiOverwrite());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, InheritedModiOverwrite.ERROR_CODE,
        String.format(InheritedModiOverwrite.ERROR_MSG_FORMAT,
            "EndTag","A4069Sub", "TEXT","EndTag", "A4069Super"), checker);
  }

}
