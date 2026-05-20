// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.values.MCValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterpreterStatementsTest extends InterpreterTestAbstract {

  @Test
  void testAssertStatementFailure() {
    MCValue MCValue = interpret("assert false;");
    assertTrue(MCValue.isError());
    assertInstanceOf(AssertionError.class, MCValue.asNativeObject());
  }

  @Test
  void testAssertStatementFailureWithMessage() {
    MCValue MCValue = interpret("assert false : \"message\";");
    assertTrue(MCValue.isError());
    assertInstanceOf(AssertionError.class, MCValue.asNativeObject());
    AssertionError error = (AssertionError) MCValue.asNativeObject();
    assertEquals("message", error.getMessage());
  }

  @Disabled
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("statementCases")
  void testStatements(String modelStr, Object expectedValue) {
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

  static Stream<Arguments> statementCases() {
    return Stream.of(
        Arguments.of("int x = 7; assert x == 7; x", 7),
        Arguments.of("int x; if (true) {x = 2;} else {x = 3;}; x", 2),
        Arguments.of("int x; if (false) {x = 2;} else {x = 3;}; x", 3),
        Arguments.of("int x = 1; if (true) {x = 2;} x", 2),
        Arguments.of("int x = 1; if (false) {x = 2;} x", 1),
        Arguments.of("int x = 1; while (x < 0) {x++;} x", 10),
        Arguments.of("int x = 1; while (false) {x++;} x", 1),
        Arguments.of("int x = 1; do {x++;} while (x < 10) x", 10),
        Arguments.of("int x = 1; do {x++;} while (false) x", 2)
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("assignmentCases")
  void testAssignmentExpressions(String modelStr, Object expectedValue) {
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

  static Stream<Arguments> assignmentCases() {
    return Stream.of(
        Arguments.of("int x = 1; x = 2; x", 2),
        Arguments.of("int x = 1; x += 2; x", 3),
        Arguments.of("int x = 2; x -= 2; x", 0),
        Arguments.of("int x = 3; x *= 2; x", 6),
        Arguments.of("int x = 8; x /= 2; x", 4),
        Arguments.of("int x = 9; x %= 4; x", 1),
        Arguments.of("int x = 1; x <<= 3; x", 8),
        Arguments.of("int x = 4; x >>= 2; x", 1),
        Arguments.of("int x = -1; x >>>= 1; x", -1 >>> 1),
        Arguments.of("int x = 6; x &= 3; x", 2),
        Arguments.of("int x = 6; x ^= 3; x", 5),
        Arguments.of("int x = 6; x |= 3; x", 7),

        Arguments.of("double x = 1.5; x += 2.0; x", 3.5),
        Arguments.of("double x = 5.0; x -= 2.5; x", 2.5),
        Arguments.of("double x = 1.5; x *= 4.0; x", 6.0),
        Arguments.of("double x = 9.0; x /= 1.5; x", 6.0),
        Arguments.of("double x = 7.5; x %= 2.0; x", 1.5),

        Arguments.of("int x = 1; ++x", 2),
        Arguments.of("int x = 1; x++", 1),
        Arguments.of("int x = 1; x++; x", 2),
        Arguments.of("int x = 2; --x", 1),
        Arguments.of("int x = 2; x--", 2),
        Arguments.of("int x = 2; x--; x", 1),

        Arguments.of("double x = 1.5; ++x", 2.5),
        Arguments.of("double x = 1.5; x++", 1.5),
        Arguments.of("double x = 1.5; x++; x", 2.5),
        Arguments.of("double x = 2.5; --x", 1.5),
        Arguments.of("double x = 2.5; x--", 2.5),
        Arguments.of("double x = 2.5; x--; x", 1.5),

        // more complex assignment
        Arguments.of(
            "int -> int s = (int n) -> n > 0 ? n + s(n - 1) : 0; s(5)"
            , 15
        )
    );
  }
}
