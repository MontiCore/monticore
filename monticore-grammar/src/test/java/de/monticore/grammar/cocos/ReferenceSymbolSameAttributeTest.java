/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.Before;
import org.junit.Test;

public class ReferenceSymbolSameAttributeTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4100.A4100";

  @Before
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
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
