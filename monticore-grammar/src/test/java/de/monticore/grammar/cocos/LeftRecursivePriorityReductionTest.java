/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LeftRecursivePriorityReductionTest extends CocoTest {
  private final String invalidGrammar = "de.monticore.grammar.cocos.invalid.A0143.A0143";

  @BeforeEach
  public void init() {
    checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new LeftRecursivePriorityReduction());
  }

  @Test
  public void testInvalid1() {
    // Priority is explicitly reduced
    testInvalidGrammar(invalidGrammar + "a", LeftRecursivePriorityReduction.ERROR_CODE,
                       String.format(LeftRecursivePriorityReduction.ERROR_MSG_FORMAT, "Expr"), checker);
  }

  @Test
  public void testInvalid2() {
    // Priority is implicitly reduced (by omitting the priority)
    testInvalidGrammar(invalidGrammar + "b", LeftRecursivePriorityReduction.ERROR_CODE,
                       String.format(LeftRecursivePriorityReduction.ERROR_MSG_FORMAT, "Expr"), checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("de.monticore.grammar.cocos.valid.A0143c", checker);
  }

}
