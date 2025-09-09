/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScopeProdOverwrittenByScopeTest extends CocoTest {

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new ScopeProdOverwrittenByScope());
  }

  @Test
  public void TestInvalid() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0275.A0275Sub", ScopeProdOverwrittenByScope.ERROR_CODE,
        String.format(ScopeProdOverwrittenByScope.ERROR_MSG_FORMAT, "Foo", "A0275Super", "Foo", "A0275Sub"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolAndScopeOverwriting", checker);
  }

}
