/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class SymbolProdOverwrittenBySymbolTest extends CocoTest {

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new SymbolProdOverwrittenBySymbol());
  }

  @Test
  public void TestInvalid() {
    testInvalidGrammar("de.monticore.grammar.cocos.invalid.A0274.A0274Sub", SymbolProdOverwrittenBySymbol.ERROR_CODE,
        String.format(SymbolProdOverwrittenBySymbol.ERROR_MSG_FORMAT, "Foo", "A0274Super", "Foo", "A0274Sub"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.SymbolAndScopeOverwriting", checker);
  }

}
