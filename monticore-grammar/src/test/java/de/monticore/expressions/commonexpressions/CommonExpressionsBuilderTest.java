/* (c) https://github.com/MontiCore/monticore */

package de.monticore.expressions.commonexpressions;

import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@TestWithMCLanguage(CommonExpressionsMill.class)
public class CommonExpressionsBuilderTest {
  @Test
  public void testBooleanAndOpExpression() {
    // Test that we can build a && expression without specifying the operator
    var elem = CommonExpressionsMill.booleanAndOpExpressionBuilder()
            .setLeft(CommonExpressionsMill.nameExpressionBuilder().setName("l").build())
            .setRight(CommonExpressionsMill.nameExpressionBuilder().setName("r").build())
            .build();
    // And that the actual operator is "&&" (instead of "" from the infix operator)
    Assertions.assertEquals("&&", elem.getOperator());
  }
}
