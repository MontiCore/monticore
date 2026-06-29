// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.rte.tuples.Tuple2;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Contains test data for Expressions test
 * containing behavior and expected results.
 * For, e.g., interpreter/code generator
 */
public class ExpressionsTestModels {

  static public Stream<Arguments> getExpressionsCases() {
    return Stream.of(
        getArithmeticCases(),
        getComparisonCases(),
        getLogicalCases(),
        getBitExpressionsCases(),
        getOptionalOperatorCases(),
        getOptionalSimilarOperatorCases(),
        getTupleCases(),
        getOCLExpressionsCases(),
        getSetExpressionsCases(),
        getTypeCastingCases()
    ).flatMap(s -> s);
  }

  static protected Stream<Arguments> getArithmeticCases() {
    return Stream.of(
        Arguments.of("42", 42),
        Arguments.of("+42", 42),
        Arguments.of("-(4)", -4),
        Arguments.of("-(-4)", 4),
        Arguments.of("1 + 3", 4),
        Arguments.of("1 + 2.5", 3.5),
        Arguments.of("2.5 + 1", 3.5),
        Arguments.of("5L + 2.0", 7.0),
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
        Arguments.of("2.0 / 8", 0.25),
        Arguments.of("7 % 3", 1),
        Arguments.of("3 % 7", 3),
        Arguments.of("7.5 % 2", 1.5),
        Arguments.of("2 % 7.5", 2.0),
        Arguments.of("+7", 7),
        Arguments.of("-7", -7),
        Arguments.of("-(-7)", 7),
        Arguments.of("+2.5", 2.5),
        Arguments.of("-2.5", -2.5),
        Arguments.of("~0", -1),
        Arguments.of("~1", -2),
        Arguments.of("~7", -8),
        Arguments.of("~(1 + 2)", -4)
    );
  }

  static protected Stream<Arguments> getComparisonCases() {
    return Stream.of(
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
        Arguments.of("false != false", false)
    );
  }

  static protected Stream<Arguments> getLogicalCases() {
    return Stream.of(
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
        // fits here somewhat-ish
        Arguments.of("true ? 5 + 2 : -1", 7)
    );
  }

  static protected Stream<Arguments> getBitExpressionsCases() {
    return Stream.of(
        Arguments.of("1 << 3", 8),
        Arguments.of("16 >> 2", 4),
        Arguments.of("16 >>> 2", 4),
        Arguments.of("6 & 3", 2),
        Arguments.of("6 ^ 3", 5),
        Arguments.of("6 | 3", 7)
    );
  }

  static protected Stream<Arguments> getOptionalOperatorCases() {
    return Stream.of(
        Arguments.of("Optional.of(2) ?: 9", 2),
        Arguments.of("((Optional<int>)Optional.empty()) ?: 9", 9),
        Arguments.of("Optional.of(2) ?< 3", true),
        Arguments.of("((Optional<int>)Optional.empty()) ?< 1", false),
        Arguments.of("Optional.of(2) ?> 3", false),
        Arguments.of("Optional.of(2) ?<= 3", true),
        Arguments.of("Optional.of(2) ?<= 1", false),
        Arguments.of("Optional.of(2) ?>= 2", true),
        Arguments.of("Optional.of(2) ?== 2", true),
        Arguments.of("Optional.of(2) ?!= 2", false)
    );
  }

  static public Stream<Arguments> getOptionalSimilarOperatorCases() {
    return Stream.of(
        Arguments.of("Optional.of(2) ?~~ 2", true),
        Arguments.of("Optional.of(2) ?~~ 3", false),
        Arguments.of("Optional.of(2) ?~~ \"2\"", false),
        Arguments.of("((Optional<int>)Optional.empty()) ?~~ 2", false),
        Arguments.of("Optional.of(2) ?!~ 2", false),
        Arguments.of("Optional.of(2) ?!~ 3", true),
        Arguments.of("Optional.of(2) ?!~ \"2\"", true),
        Arguments.of("((Optional<int>)Optional.empty()) ?!~ 2", false)
    );
  }

  static protected Stream<Arguments> getTupleCases() {
    return Stream.of(
        Arguments.of("(0, 3)", Tuple2.of(0, 3)),
        Arguments.of("(0, 3)[1]", 3),
        // specifically this case broke once,
        // due to a combination of CallExpression CTTI and Tuples
        Arguments.of(
            "(((byte, int) t) -> (0, t)) (((byte)1, 2))",
            Tuple2.of(0, Tuple2.of((byte) 1, 2))
        )
    );
  }

  static public Stream<Arguments> getOCLExpressionsCases() {
    return Stream.of(
        Arguments.of("if true then 5 else 2", 5),
        Arguments.of("true implies false", false),
        Arguments.of("true <=> false", false),
        Arguments.of("true <=> true", true),
        Arguments.of("false <=> true", false),
        Arguments.of("forall int x in [1, 2, 3] : x > 0", true),
        Arguments.of("forall int x in [1, 2, 3] : x < 0", false),
        Arguments.of("exists int x in [1, 2, 3] : x == 2", true),
        Arguments.of("exists int x in [1, 2, 3] : x == 4", false),
        Arguments.of("let int x = 5 in x + 2", 7),
        Arguments.of("let int x = 5; int y = 2 + x in x + y", 12),
        Arguments.of(
            "iterate { int x in [1, 2, 3]; int sum = 0 : sum = sum + x }",
            6
        ),
        Arguments.of("2 isin [1, 2, 3]", true),
        Arguments.of("2 notin [1, 2, 3]", false),
        Arguments.of("setand [true, true, false]", false),
        Arguments.of("setor [false, false, true]", true),
        Arguments.of("1 isin union {{1}, {2}, {3}}", true),
        Arguments.of("1 isin intersect {{1, 2}, {1, 3}}", true),
        Arguments.of("(any [1, 2]) isin [1, 2]", true),
        Arguments.of("(any [1, 2]) isin [2, 1]", true),
        Arguments.of("(any [1]) isin [2]", false)
    );
  }

  static protected Stream<Arguments> getSetExpressionsCases() {
    return Stream.of(
        Arguments.of("[1, 4..7, 33]",
            List.of(1, 4, 5, 6, 7, 33)),
        Arguments.of("[x + x | x in [1, 4..7, 33]]",
            List.of(2, 8, 10, 12, 14, 66)),
        Arguments.of("[x + x | x in [y + y | y in [1,2,3]]]",
            List.of(4, 8, 12)),
        Arguments.of(
            "[z + x | int x in [y + y | y in [1,2,3]], "
                + "int z in [1 + x, 2 + x]]",
            List.of(5, 6, 9, 10, 13, 14)),
        Arguments.of("3 isin ({1, 2} union {2, 3})", true),
        Arguments.of("5 isin ({1, 2} union {2, 3})", false),
        Arguments.of("1 isin ({1, 2} intersect {2, 3})", false),
        Arguments.of("2 isin ({1, 2} intersect {2, 3})", true),
        Arguments.of("1 isin ({1, 2, 3} \\ {2, 3})", true),
        Arguments.of("2 isin ({1, 2, 3} \\ {2, 3})", false),
        Arguments.of("[z | x in [1, 2, 3], int z = x + 1]",
            List.of(2, 3, 4)),
        Arguments.of("[x in [1,3] | true]", List.of(1, 3)),
        Arguments.of("[x in [1,2,3] | x % 2 == 1]", List.of(1, 3)),
        Arguments.of("{1, 4..6, 4}", Set.of(1, 4, 5, 6)),
        Arguments.of("{x * x | x in {1, 2, 3}}", Set.of(1, 4, 9)),
        Arguments.of("{x | x in {1, 2, 3, 4}, x % 2 == 0}", Set.of(2, 4))
    );
  }

  static public Stream<Arguments> getTypeCastingCases() {
    return Stream.of(
        Arguments.of("((int) 5.2)", 5),
        Arguments.of("(String)\"Hello\"", "Hello"),
        // Tuple
        Arguments.of("((double, double)) (0, 3)", Tuple2.of(0.0, 3.0)),
        Arguments.of("((double, double)) (((int, double)) (0, 3))", Tuple2.of(0.0, 3.0)),
        Arguments.of("(((double, int), double)) ((0, 0), 3)", Tuple2.of(Tuple2.of(0.0, 0), 3.0)),
        // Union (only trivial for now)
        Arguments.of("(double | float) 5", 5.0),
        // Lambda
        Arguments.of("((() -> double) (() -> 5))()", 5.0),
        Arguments.of("((int -> double) ((int x) -> x + 1))(2)", 3.0),
        Arguments.of("(((int, int) -> double) ((int x, int y) -> x + y))(2, 3)", 5.0),
        Arguments.of("(((boolean, int, int) -> double) ((boolean b, int x, int y) -> b ? x : y))(true, 1, 2)", 1.0),
        // Tuple + Union
        Arguments.of("(((double | int), double)) (0, 3)", Tuple2.of(0.0, 3.0)),
        // Union + Lambda
        Arguments.of("((() -> (double | float)) () ->  5)() + 2", 7.0),
        Arguments.of("((((() -> double) | (() -> float))) () ->  5)() + 2", 7.0)
    );
  }

}
