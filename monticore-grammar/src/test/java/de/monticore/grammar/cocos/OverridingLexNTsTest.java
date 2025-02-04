/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OverridingLexNTsTest extends CocoTest{

  private final String MESSAGE =  " The lexical production CARDINALITY must not use a different "
      + "type to store the token than the overridden production.";
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4026.A4026b";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingLexNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, OverridingLexNTs.ERROR_CODE, String.format(MESSAGE, "interface"),
        checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Overriding", checker);
  }

}
