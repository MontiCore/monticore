// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterExpressionsTest extends InterpreterTestAbstract {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("expressionCases")
  void testExpressions(String exprStr, Object expectedValue) {
    assertEquals(expectedValue, interpretAndCast(exprStr));
  }

  // checks if the interpreter also works while logging
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("expressionCases")
  void testExpressionsWithLog(String exprStr, Object expectedValue) {
    interpreter = initializeInterpreterWithLog();
    assertEquals(expectedValue, interpretAndCast(exprStr));
  }

  static Stream<Arguments> expressionCases() {
    return Stream.of(
        Arguments.of("1 + 3", 4),
        Arguments.of("1 + 2.5", 3.5),
        Arguments.of("2.5 + 1", 3.5),
        Arguments.of("\"a\" + \"b\"", "ab"),
        Arguments.of("\"x\" + 1", "x1"),
        Arguments.of("1 + \"x\"", "1x"),
        Arguments.of("\"sum=\" + (1 + 2)", "sum=3"),
        Arguments.of("7 - 2", 5),
        Arguments.of("2 - 7", -5),
        Arguments.of("7 - 2.5", 4.5),
        Arguments.of("2.5 - 7", -4.5),
        Arguments.of("3 * 4", 12),
        Arguments.of("1.5 * 4", 6.0),
        Arguments.of("4 * 1.5", 6.0),
        Arguments.of("7 / 2", 3),
        Arguments.of("2 / 7", 0),
        Arguments.of("7 / 2.0", 3.5),
        Arguments.of("2.0 / 7", 2.0 / 7.0),
        Arguments.of("7 % 3", 1),
        Arguments.of("3 % 7", 3),
        Arguments.of("7.5 % 2", 1.5),
        Arguments.of("2 % 7.5", 2.0),
        Arguments.of("+7", 7),
        Arguments.of("-7", -7),
        Arguments.of("+2.5", 2.5),
        Arguments.of("-2.5", -2.5),
        Arguments.of("1 < 3", true),
        Arguments.of("3 < 1", false),
        Arguments.of("1 <= 1", true),
        Arguments.of("2 <= 1", false),
        Arguments.of("3 > 1", true),
        Arguments.of("1 > 3", false),
        Arguments.of("3 >= 3", true),
        Arguments.of("1 >= 3", false),
        Arguments.of("1 < 1.5", true),
        Arguments.of("1.5 < 1", false),
        Arguments.of("2.5 <= 2.5", true),
        Arguments.of("2.5 <= 2", false),
        Arguments.of("3.5 > 3", true),
        Arguments.of("3 > 3.5", false),
        Arguments.of("3.5 >= 3.5", true),
        Arguments.of("3 >= 3.5", false),
        Arguments.of("1 == 1", true),
        Arguments.of("1 == 2", false),
        Arguments.of("1 != 2", true),
        Arguments.of("2 != 2", false),
        Arguments.of("1 == 1.0", true),
        Arguments.of("1.0 == 1", true),
        Arguments.of("1.5 == 1.5", true),
        Arguments.of("1.5 == 1", false),
        Arguments.of("1 != 1.0", false),
        Arguments.of("3.0 != 3", false),
        Arguments.of("1.5 != 1", true),
        Arguments.of("true == true", true),
        Arguments.of("true == false", false),
        Arguments.of("false == true", false),
        Arguments.of("true != false", true),
        Arguments.of("false != false", false),
        Arguments.of("true && true", true),
        Arguments.of("true && false", false),
        Arguments.of("false && true", false),
        Arguments.of("false && false", false),
        Arguments.of("true || true", true),
        Arguments.of("true || false", true),
        Arguments.of("false || true", true),
        Arguments.of("false || false", false),
        Arguments.of("!true", false),
        Arguments.of("!false", true),
        Arguments.of("!(1 < 2)", false),
        Arguments.of("!(2 < 1)", true),
        Arguments.of("~0", -1),
        Arguments.of("~1", -2),
        Arguments.of("~7", -8),
        Arguments.of("~(1 + 2)", -4),
        Arguments.of("1 << 3", 8),
        Arguments.of("16 >> 2", 4),
        Arguments.of("16 >>> 2", 4),
        Arguments.of("6 & 3", 2),
        Arguments.of("6 ^ 3", 5),
        Arguments.of("6 | 3", 7),
        Arguments.of("Optional.of(2) ?<= 3", true),
        Arguments.of("Optional.of(2) ?<= 1", false),
        Arguments.of("((Optional<int>)Optional.empty()) ?<= 1", false),
        Arguments.of("(String)\"Hello\"", "Hello"),
        Arguments.of("(() -> 1)()", 1),
        Arguments.of("(() -> () -> 2)()()", 2),
        Arguments.of("((long a) -> a + 1)(41L)", 42),
        Arguments.of("((long a) -> (byte b) -> a + b)(4L)((byte)28)", 32),
        Arguments.of("((long a, byte b) -> a + b)(4L,(byte)28)", 32),
        Arguments.of("(() -> () -> (int a) -> () -> () -> a)()()(42)()()", 42),
        Arguments.of("((byte b) -> (char c) -> b + c)((byte)25)('a') == 'z'", true),
        Arguments.of("[1, 4..7, 33]",
            List.of(1, 4, 5, 6, 7, 33)),
        Arguments.of("[x + x | x in [1, 4..7, 33]]",
            List.of(2, 8, 10, 12, 14, 66)),
        Arguments.of("[x + x | x in [y + y | y in [1,2,3]]]",
            List.of(4, 8, 12)),
        Arguments.of(
            "[z + x | int x in [y + y | y in [1,2,3]], "
                + "int z in [1 + x, 2 + x]]",
            List.of(5, 6, 9, 10, 13, 14))
    );
  }

}
