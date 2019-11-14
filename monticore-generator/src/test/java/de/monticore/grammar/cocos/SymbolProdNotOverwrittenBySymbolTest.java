// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class SymbolProdNotOverwrittenBySymbolTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new SymbolProdNotOverwrittenBySymbol());
  }

  @Test
  public void TestInvalid() {
    testInvalidGrammar("cocos.invalid.A0274.A0274Sub", SymbolProdNotOverwrittenBySymbol.ERROR_CODE,
        String.format(SymbolProdNotOverwrittenBySymbol.ERROR_MSG_FORMAT, "Foo", "A0274Super", "Foo", "A0274Sub"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.SymbolOverwriting", checker);
  }

}
