package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReferenceSymbolNotNameTest extends CocoTest {
  private final String MESSAGE = " You can only refer to other symbols on the nonterminal Name.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4039.A4039";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ReferenceSymbolNotName());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar , ReferenceSymbolNotName.ERROR_CODE,
        MESSAGE, checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.ReferencedSymbol", checker);
  }

}
