package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReferenceSymbolNotNameTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4039.A4039";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ReferenceSymbolNotName());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar , ReferenceSymbolNotName.ERROR_CODE,
        ReferenceSymbolNotName.ERROR_MSG_FORMAT, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.ReferencedSymbol", checker);
  }

}
