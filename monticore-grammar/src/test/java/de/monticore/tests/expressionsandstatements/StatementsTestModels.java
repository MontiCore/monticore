// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Contains test data for Statements test
 * containing behavior and expected results.
 * For, e.g., interpreter/code generator
 */
public class StatementsTestModels {

  static public Stream<Arguments> getStatementCases() {
    return Stream.of(
        getCommonStatementCases(),
        getAssignmentCases(),
        getValidAssertStatementsCases()
    ).flatMap(Function.identity());
  }

  /**
   * Like {@link #getStatementCases()} but without constructs not supported by the interpreter.
   * The interpreter test suite uses this to keep statement test models parser-compatible.
   */
  static public Stream<Arguments> getInterpreterStatementCases() {
    return Stream.of(
        getCommonStatementCases().filter(c -> {
          final String modelStr = (String) c.get()[0];
          // The test language does not support local array variable declarations like "int[] a".
          return !modelStr.contains("int[] ");
        }),
        getAssignmentCases(),
        getValidAssertStatementsCases()
    ).flatMap(Function.identity());
  }

  static protected Stream<Arguments> getCommonStatementCases() {
    return Stream.of(
        Arguments.of("int x; if (true) {x = 2;} else {x = 3;}; x", 2),
        Arguments.of("int x; if (false) {x = 2;} else {x = 3;}; x", 3),
        Arguments.of("int x = 1; if (true) {x = 2;} x", 2),
        Arguments.of("int x = 1; if (false) {x = 2;} x", 1),
        Arguments.of("int x = 1; while (x < 0) {x++;} x", 1),
        Arguments.of("int x = 1; while (false) {x++;} x", 1),
        Arguments.of("int x = 0; while (true) {x++; break; x++;} x", 1),
        Arguments.of("int x = 1; do {x++;} while (x < 10); x", 10),
        Arguments.of("int x = 1; do {x++;} while (false); x", 2),
        Arguments.of("int x = 0; do {x++; break; x++;} while (true); x", 1),
        Arguments.of("int x = 0; for (int i = 0; i < 4; i++) x++; x", 4),
        Arguments.of("int x = 0, i = 0; for (x++, i = 0; i < 4; i++); x", 1),
        Arguments.of("int x = 0; for (int i = 0; i < 4; i++, x++); x", 4),
        Arguments.of("int x = 0; for (;x < 4;) x++; x", 4),
        Arguments.of("int x = 0; for (; ++x < 4;); x", 4),
        Arguments.of("int x = 0; for (; x++ < 4;); x", 5),
        Arguments.of("int x = 1; for (int i = 0; i < 0; i++) x++; x", 1),
        Arguments.of("int[] a = {1,2,3}; int x = 0; for (int e : a) x += e; x", 6),
        Arguments.of("int[] a = new int[0]; int x = 0; for(int e : a) x+= e; x", 0),
        Arguments.of("int x = 0; for(int e : [1,2,3]) x+= e; x", 6),
        Arguments.of("int x = 0; for(int e : (List<int>)[]) x+= e; x", 0),
        Arguments.of("""
            int x = 0;
            for (int i = 0; i < 2; i++)
              for (int j = 0; j < 3; j++)
                x++;
            x
            """, 6),
        Arguments.of("int x = 1; ; ; ; x", 1)
    );
  }

  static protected Stream<Arguments> getAssignmentCases() {
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

        // multiple assignments in one statement
        Arguments.of("int x = 1, y = 2; x + y", 3),
        Arguments.of("int a, b = 4; a = 2; a + b", 6),

        // more complex assignment
        Arguments.of(
            "int -> int s = (int n) -> n > 0 ? n + s(n - 1) : 0; s(5)"
            , 15
        )
    );
  }

  static protected Stream<Arguments> getValidAssertStatementsCases() {
    // invalid cases have different behavior for JavaGen and Interpreter
    return Stream.of(
        Arguments.of("int x = 7; assert x == 7; x", 7)
    );
  }

}
