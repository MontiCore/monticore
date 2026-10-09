// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.rte.tuples.Tuple2;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * Contains test data for SIUnit tests.
 * SIUnits are not represented at runtime, values are in the units
 * of their (non-normalized) types, e.g., 5km -> 5, 5km + 3m -> 5003 (m).
 */
public class SIUnitTestModels {

  static public Stream<Arguments> getSIUnitCases() {
    return Stream.of(
        getLiteralCases(),
        getArithmeticCases(),
        getComparisonCases(),
        getDeclarationCases(),
        getAssignmentCases(),
        getConditionalCases(),
        getFunctionCases(),
        getAccessCases(),
        getTupleCases(),
        getUnionCases()
    ).flatMap(s -> s);
  }

  static protected Stream<Arguments> getAccessCases() {
    return Stream.of(
        Arguments.of("[km]<int>[] a = new [km]<int>[1]; a[0] = 2km; a[0]", 2),
        Arguments.of("[km]<int>[] a = new [km]<int>[1]; a[0] = 2km; a[0] + 0m",
            2000),
        Arguments.of("[km]<int>[] a = new [km]<int>[1]; a[0] = 2000m; "
            + "[km]<int> x = a[0]; x", 2)
    );
  }

  static protected Stream<Arguments> getTupleCases() {
    return Stream.of(
        Arguments.of("(1km, 2)", Tuple2.of(1, 2)),
        Arguments.of("(1km, 2m)", Tuple2.of(1, 2)),
        Arguments.of("(1.5km, 2h)", Tuple2.of(1.5, 2)),
        Arguments.of("(1km, 2)[0]", 1),
        Arguments.of("(1km, 2m)[1]", 2),
        Arguments.of("([km]<int>, int) t = (1km, 2); t[0]", 1),
        Arguments.of("([km]<int>, int) t = (1km, 2); t[0] + 1m", 1001),
        Arguments.of("([km]<int>, int) t = (1500m, 2); "
            + "[km]<double> x = t[0]; x", 1.0),
        Arguments.of("([m]<int>, int) t = (1km, 2); t", Tuple2.of(1000, 2)),
        Arguments.of("([m]<int>, int) t = (1km, 2); t[0]", 1000),
        Arguments.of("([km]<double>, [s]<int>) t = (1500m, 2min); t",
            Tuple2.of(1.5, 120)),
        Arguments.of("([m]<int>, int) t = (1500mm, 2); t", Tuple2.of(1, 2)),
        Arguments.of("([km]<int>, int) t = (1km, 2); "
            + "([m]<long>, int) u = t; u", Tuple2.of(1000L, 2)),
        Arguments.of("([m]<int>, int) t = (1m, 2); t = (2km, 3); t",
            Tuple2.of(2000, 3)),
        Arguments.of("(([km]<int>, int), int) t = ((1km, 2), 3); t[0][0]", 1),
        Arguments.of("(([km]<int>, int), int) t = ((1km, 2), 3); "
            + "(([m]<int>, int), int) u = t; u",
            Tuple2.of(Tuple2.of(1000, 2), 3)),
        Arguments.of("(1km, 2) == (1000m, 2)", true),
        Arguments.of("(1km, 2) == (1m, 2)", false),
        Arguments.of("true ? (1km, 2) : (1m, 2)", Tuple2.of(1000, 2)),
        Arguments.of("false ? (1km, 2) : (1m, 2)", Tuple2.of(1, 2)),
        Arguments.of("[km]<int> -> ([m]<int>, int) f = "
            + "([km]<int> x) -> (x, 1); f(2km)", Tuple2.of(2000, 1)),
        Arguments.of("(([km]<int>, int)) -> [m]<int> f = "
            + "(([km]<int>, int) t) -> t[0] + 0m; f((3000m, 1))", 3000)
    );
  }

  /**
   * values of unions are in the units of the normalized types
   */
  static protected Stream<Arguments> getUnionCases() {
    return Stream.of(
        Arguments.of("([km]<int> | [m]<int>) x = 1km; x", 1000),
        Arguments.of("([km]<int> | [m]<int>) x = 1500mm; x", 1),
        Arguments.of("([km]<int> | [m]<int>) x = 1km; x + 1m", 1001),
        Arguments.of("([km]<int> | [m]<int>) x = 1km; x == 1000m", true),
        Arguments.of("([km]<int> | [m]<int>) x = 1km; [km]<int> y = x; y", 1),
        Arguments.of("([km]<int> | [km]<int>) x = 2km; x", 2000),
        Arguments.of("([km]<int> | [m]<double>) x = 1km; x", 1000.0),
        Arguments.of("([km]<int> | [s]<int>) x = 1km; x", 1000),
        Arguments.of("([km]<int> | [s]<int>) x = 1min; x", 60),
        Arguments.of("([km]<int> | String) x = 1km; x", 1000),
        Arguments.of("([km]<int> | String) x = \"a\"; x", "a"),
        Arguments.of("(([km]<int> | [m]<int>), int) t = (1km, 2); t",
            Tuple2.of(1000, 2)),
        Arguments.of("([km]<int> | [m]<int>) -> [m]<int> f = "
            + "(([km]<int> | [m]<int>) x) -> x + 0m; f(1km)", 1000)
    );
  }

  static protected Stream<Arguments> getLiteralCases() {
    return Stream.of(
        Arguments.of("5km", 5),
        Arguments.of("5m", 5),
        Arguments.of("2.5km", 2.5),
        Arguments.of("30 km^2", 30)
    );
  }

  static protected Stream<Arguments> getArithmeticCases() {
    return Stream.of(
        Arguments.of("5km + 3m", 5003),
        Arguments.of("3m + 5km", 5003),
        Arguments.of("5km - 3m", 4997),
        Arguments.of("1km + 1.5m", 1001.5),
        Arguments.of("1h + 30min", 5400),
        Arguments.of("1500.0g + 1kg", 2.5),
        Arguments.of("2km * 3m", 6000),
        Arguments.of("2km * 3", 6000),
        Arguments.of("3 * 2km", 6000),
        Arguments.of("6km / 2", 3000),
        Arguments.of("1km / 1m", 1000),
        Arguments.of("1km / 1s", 1000),
        Arguments.of("2 / 1ms", 2000),
        Arguments.of("7km % 3m", 1),
        // scaled operands are calculated in double, then cast back
        Arguments.of("999mm + 999mm", 1),
        Arguments.of("1mm + 1m", 1),
        Arguments.of("1500mm * 2", 3),
        Arguments.of("[mm]<int> x = 1mm; x * 1000", 1),
        Arguments.of("-(5km)", -5000),
        Arguments.of("-5km", -5000),
        Arguments.of("+(5km)", 5000),
        Arguments.of("(5km)", 5)
    );
  }

  static protected Stream<Arguments> getComparisonCases() {
    return Stream.of(
        Arguments.of("1km > 999m", true),
        Arguments.of("1km < 999m", false),
        Arguments.of("1km >= 1000m", true),
        Arguments.of("1km <= 999m", false),
        Arguments.of("1km == 1000m", true),
        Arguments.of("1km != 1000m", false),
        Arguments.of("1km == 1m", false),
        Arguments.of("60min == 1h", true),
        Arguments.of("1mm == 0m", false),
        Arguments.of("1mm > 0m", true)
    );
  }

  static protected Stream<Arguments> getDeclarationCases() {
    return Stream.of(
        Arguments.of("[km]<int> x = 2km; x", 2),
        Arguments.of("[km]<int> x = 1500m; x", 1),
        Arguments.of("[km]<double> x = 1500m; x", 1.5),
        Arguments.of("[m]<int> x = 1km; x", 1000),
        Arguments.of("[km]<int> x = 2km; [m]<int> y = x; y", 2000),
        Arguments.of("[km]<int> x = 2km; x + 1m", 2001),
        Arguments.of("[km]<long> x = 2km; [m]<long> y = x; y", 2000L)
    );
  }

  static protected Stream<Arguments> getAssignmentCases() {
    return Stream.of(
        Arguments.of("[m]<int> x = 1m; x = 2km; x", 2000),
        Arguments.of("[km]<int> x = 1km; x = 2000m; x", 2),
        // the type of the assignment expression is normalized
        Arguments.of("[km]<int> x = 1km; x = 2000m", 2000),
        Arguments.of("[m]<int> x = 1m; x += 1km; x", 1001),
        Arguments.of("[km]<double> x = 1km; x += 500m; x", 1.5),
        Arguments.of("[km]<int> x = 3km; x -= 1000m; x", 2),
        Arguments.of("[km]<int> x = 3km; x *= 2; x", 6),
        Arguments.of("[km]<int> x = 6km; x /= 2; x", 3),
        Arguments.of("[km]<int> x = 7km; x %= 3km; x", 1),
        Arguments.of("[km]<int> x = 1km; [m]<int> y = x = 3km; y", 3000),
        Arguments.of("[km]<int> x = 0km; "
            + "for (int i = 0; i < 3; i++) x += 1000m; x", 3),
        Arguments.of("int n = 0; "
            + "for ([km]<int> x = 0km; x < 3000m; x += 1000m) n++; n", 3)
    );
  }

  static protected Stream<Arguments> getConditionalCases() {
    return Stream.of(
        Arguments.of("true ? 1km : 1m", 1000),
        Arguments.of("false ? 1km : 1m", 1),
        Arguments.of("[km]<int> x = true ? 1km : 1m; x", 1)
    );
  }

  static protected Stream<Arguments> getFunctionCases() {
    return Stream.of(
        Arguments.of("[km]<int> -> [km]<int> f = ([km]<int> x) -> x; "
            + "[km]<int> r = f(1000m); r", 1),
        Arguments.of("[km]<int> -> [km]<int> f = ([km]<int> x) -> x; "
            + "f(1000m) == 1km", true),
        Arguments.of("[km]<int> -> [m]<int> f = ([km]<int> x) -> x + 0m; "
            + "f(2km)", 2000)
    );
  }

}
