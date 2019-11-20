// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScopeProdOverwrittenByScopeTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ScopeProdOverwrittenByScope());
  }

  @Test
  public void TestInvalid() {
    testInvalidGrammar("cocos.invalid.A0275.A0275Sub", ScopeProdOverwrittenByScope.ERROR_CODE,
        String.format(ScopeProdOverwrittenByScope.ERROR_MSG_FORMAT, "Foo", "A0275Super", "Foo", "A0275Sub"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.SymbolAndScopeOverwriting", checker);
  }

}