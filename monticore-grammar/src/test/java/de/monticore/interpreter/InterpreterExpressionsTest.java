// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.tests.expressionsandstatements.ExpressionsTestModels;
import de.monticore.values.MCValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class InterpreterExpressionsTest extends AbstractInterpreterTest {

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getExpressionsCases")
  void testExpressions(String modelStr, Object expectedValue) {
    assumeSupported(modelStr, expectedValue);
    checkValue(modelStr, expectedValue);
  }

  // checks if the interpreter also works while logging
  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getExpressionsCases")
  void testExpressionsWithLog(String modelStr, Object expectedValue) {
    assumeSupported(modelStr, expectedValue);
    MCValue value = interpret(modelStr, true);
    checkValue(value, expectedValue);
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

  protected void assumeSupported(String exprStr, Object expectedValue) {
    // Tuples not supported yet
    assumeFalse(exprStr.contains("(0, 3)"));
    assumeFalse(exprStr.contains("((0, 0), 3)"));
    assumeFalse(exprStr.contains("(0, t)"));
    // OCLExpressions are not supported yet
    assumeFalse(
        ExpressionsTestModels.getOCLExpressionsCases()
            .anyMatch(c -> c.get()[0].equals(exprStr))
    );
    // UglyExpressions are not supported yet
    assumeFalse(
        ExpressionsTestModels.getTypeCastingCases()
            .anyMatch(c -> c.get()[0].equals(exprStr))
    );
    // some SetExpressions are not supported
    assumeFalse(exprStr.contains("} union {"));
    assumeFalse(exprStr.contains("} intersect {"));
    assumeFalse(exprStr.contains("} \\ {"));
    assumeFalse(exprStr.contains("[z | x in "));
  }

}
