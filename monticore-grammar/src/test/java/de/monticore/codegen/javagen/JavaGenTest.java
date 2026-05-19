// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class JavaGenTest extends AbstractJavaGenTest {

  // small set of tests for the alpha version of the generator,
  // to be severely extended in the future.

  @ParameterizedTest
  @MethodSource
  public void testJavaGenExprVals(String exprStr, String expectedValue) {
    checkValue(exprStr, expectedValue);
  }

  protected static Stream<Arguments> testJavaGenExprVals() {
    return Stream.of(
        // Simple expressions
        Arguments.of("10", "10"),
        Arguments.of("-(-3)", "3"),
        Arguments.of("5 + 2", "7"),
        Arguments.of("5L + 2.0", "7.0"),
        Arguments.of("((java.lang.Integer) 5) + 2.0", "7.0"),
        Arguments.of("\"a\" + 2", "\"a2\""),
        Arguments.of("true ? 5 + 2 : -1", "7"),
        Arguments.of("java.lang.Integer.MAX_VALUE", String.valueOf(Integer.MAX_VALUE)),
        Arguments.of("java.lang.Math.abs(-2)", "2"),
        Arguments.of("\"myString\" + 5", "\"myString5\""),
        // Tuple
        Arguments.of("(0, 3)", "(0, 3)"),
        Arguments.of("(0, 3)[1]", "3"),
        Arguments.of("((double, double)) (0, 3)", "(0.0, 3.0)"),
        Arguments.of("((double, double)) (((int, double)) (0, 3))", "(0.0, 3.0)"),
        Arguments.of("(((double, int), double)) ((0, 0), 3)", "((0.0, 0), 3.0)"),
        // Union (only trivial for now)
        Arguments.of("(double | float) 5", "5.0"),
        // Lambda
        Arguments.of("((() -> double) (() -> 5))()", "5.0"),
        Arguments.of("((int -> double) ((int x) -> x + 1))(2)", "3.0"),
        Arguments.of("(((int, int) -> double) ((int x, int y) -> x + y))(2, 3)", "5.0"),
        Arguments.of("(((boolean, int, int) -> double) ((boolean b, int x, int y) -> b ? x : y))(true, 1, 2)", "1.0"),
        // Tuple + Union
        Arguments.of("(((double | int), double)) (0, 0)", "(0.0, 0.0)"),
        // Union + Lambda
        Arguments.of("((() -> (double | float)) () ->  5)() + 2", "7.0"),
        Arguments.of("((((() -> double) | (() -> float))) () ->  5)() + 2", "7.0"),
        // functions
        Arguments.of("((() -> double) (() -> 5))()", "5.0"),
        Arguments.of("((int -> double) ((int x) -> x + 1))(2)", "3.0"),
        Arguments.of("(((int, int) -> double) ((int x, int y) -> x + y))(2, 3)", "5.0"),
        Arguments.of("(((boolean, int, int) -> float) ((boolean b, int x, int y) -> b ? x : y))(true, 1, 2)", "1.0")
    );
  }

}
