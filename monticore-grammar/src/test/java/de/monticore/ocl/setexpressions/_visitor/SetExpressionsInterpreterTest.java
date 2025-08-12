package de.monticore.ocl.setexpressions._visitor;

import de.monticore.expressions.AbstractExpressionInterpreterTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class SetExpressionsInterpreterTest extends AbstractExpressionInterpreterTest {

  @Test
  public void testInterpretSetEnumerationExpression() {
    testInvalidExpression("1..2");

    testValidExpression("[1..7]",
            createValue(List.of(1, 2, 3, 4, 5, 6, 7)));
    testValidExpression("{1..10}",
            createValue(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
  }

  @Test
  public void testInterpretSetComprehensionExpression() {
    testInvalidExpression("{| x in [1..2]}");
    testInvalidExpression("{x | }");
    testInvalidExpression("{x | x in [1..2], x = x}");

    testValidExpression("{x | x in [1..3]}", createValue(Set.of(1, 2, 3)));
    testValidExpression("[x * x | x in [1..3]]",
            createValue(List.of(1, 4, 9)));

    testValidExpression("{x | x in [1..3], x % 2 == 1}",
            createValue(Set.of(1, 3)));

    testValidExpression("{x * y | x in [1..3], y in [1..2], x != y}",
            createValue(Set.of(2, 3, 6)));

    testValidExpression("{y | x in [1..3], int y = x * x * x}",
            createValue(Set.of(1, 8, 27)));
  }

}
