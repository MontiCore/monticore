// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class SymbolRuleWithoutSymbolRefTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A0117.A0117";
  private final String grammar2 = "cocos.invalid.A0117.A0117a";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new SymbolRuleWithoutSymbolRef());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, SymbolRuleWithoutSymbolRef.ERROR_CODE,
            String.format(SymbolRuleWithoutSymbolRef.ERROR_MSG_FORMAT, "B"), checker);
  }

  @Test
  public void testInvalid2() {
    testInvalidGrammar(grammar2, SymbolRuleWithoutSymbolRef.ERROR_CODE,
            String.format(SymbolRuleWithoutSymbolRef.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
