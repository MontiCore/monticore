/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NoOverridingNTHasAnnotationTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4094.A4094";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new NoOverridingNTHasAnnotation());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, NoOverridingNTHasAnnotation.ERROR_CODE,
        String.format(NoOverridingNTHasAnnotation.ERROR_MSG_FORMAT, "Foo"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
