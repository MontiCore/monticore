// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.tests.expressionsandstatements.ExpressionsTestModels;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class InterpreterExpressionsTest extends AbstractInterpreterTest {

  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getExpressionsCases")
  void testExpressions(String exprStr, Object expectedValue) {
    assumeSupported(exprStr, expectedValue);
    assertEquals(expectedValue, interpretAndCast(exprStr));
  }

  // checks if the interpreter also works while logging
  @ParameterizedTest
  @MethodSource("de.monticore.tests.expressionsandstatements.ExpressionsTestModels#getExpressionsCases")
  void testExpressionsWithLog(String exprStr, Object expectedValue) {
    assumeSupported(exprStr, expectedValue);
    interpreter = initializeInterpreterWithLog();
    assertEquals(expectedValue, interpretAndCast(exprStr));
  }

  protected void assumeSupported(String exprStr, Object expectedValue) {
    // Tuples not supported yet
    assumeFalse(exprStr.contains("(0, 3)"));
    assumeFalse(exprStr.contains("((0, 0), 3)"));
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
  }

}
