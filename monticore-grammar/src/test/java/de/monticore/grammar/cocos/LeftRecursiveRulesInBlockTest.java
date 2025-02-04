/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LeftRecursiveRulesInBlockTest extends CocoTest {
  private final String grammar = "de.monticore.grammar.cocos.invalid.A4056.A4056";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new LeftRecursiveRulesInBlock());
  }

  @Test
  public void testSimpleLeftRecursion() {
    testInvalidGrammar(grammar, LeftRecursiveRulesInBlock.ERROR_CODE,
        String.format(LeftRecursiveRulesInBlock.ERROR_MSG_FORMAT, "A"), checker);
  }

  @Test
  public void testComplexLeftRecursion() {
    testInvalidGrammar(grammar+"a", LeftRecursiveRulesInBlock.ERROR_CODE,
            String.format(LeftRecursiveRulesInBlock.ERROR_MSG_FORMAT, "PlusExpression"), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Attributes", checker);
  }

}
