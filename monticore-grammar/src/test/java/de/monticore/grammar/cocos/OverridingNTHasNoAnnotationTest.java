/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class OverridingNTHasNoAnnotationTest extends CocoTest{
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4098.A4098";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingNTHasNoAnnotation());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingNTHasNoAnnotation.ERROR_CODE,
        String.format(OverridingNTHasNoAnnotation.ERROR_MSG_FORMAT, "Foo", "de.monticore.grammar.cocos.invalid.A4098.A4098Super.Foo" ), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
