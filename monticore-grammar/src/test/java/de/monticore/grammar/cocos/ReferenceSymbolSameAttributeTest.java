/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class ReferenceSymbolSameAttributeTest extends CocoTest {

  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4100.A4100";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new ReferenceSymbolSameAttribute());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ReferenceSymbolSameAttributeVisitor.ERROR_CODE,
        String.format(ReferenceSymbolSameAttributeVisitor.ERROR_MSG_FORMAT, "\"ref\"", "A","B"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.ReferencedSymbol", checker);
  }

}
