// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.rte.tuples.Tuple2;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * Contains test data for SIUnit tests.
 * SIUnits are not represented at runtime, values are in the units
 * of their types, e.g., 5km -> 5.0, 5km + 3m -> 5.003 (km).
 * The expected values are calculated the same way as the generated code,
 * e.g., 5 + 3 / 1000.0, as the double values are compared exactly.
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

  static protected Stream<Arguments> getLiteralCases() {
    return Stream.of(
        Arguments.of("5km", 5.0),
        Arguments.of("5m", 5.0),
        Arguments.of("2.5km", 2.5),
        Arguments.of("2.5f km", 2.5f),
        Arguments.of("30 km^2", 30.0),
        Arguments.of("5km / 2", 2.5)
    );
  }

  static protected Stream<Arguments> getArithmeticCases() {
    return Stream.of(
        // the larger unit
        Arguments.of("5km + 3m", 5 + 3 / 1000.0),
        Arguments.of("3m + 5km", 3 / 1000.0 + 5),
        Arguments.of("5km - 3m", 5 - 3 / 1000.0),
        Arguments.of("1mm + 1m", 1 / 1000.0 + 1),
        Arguments.of("1h + 30min", 1 + 30 / 60.0),
        Arguments.of("1500.0g + 1kg", 1500.0 / 1000 + 1),
        Arguments.of("1kg + 1500.0g", 1 + 1500.0 / 1000),
        // the smaller unit
        Arguments.of("7km % 3m", (7 * 1000.0) % 3),
        Arguments.of("7m % 3km", 7 % (3 * 1000.0)),
        // the units of the operands
        Arguments.of("2km * 3", 6.0),
        Arguments.of("3 * 2km", 6.0),
        Arguments.of("6km / 4", 1.5),
        Arguments.of("1km / 1s", 1.0),
        Arguments.of("2 / 1ms", 2.0),
        Arguments.of("2km^2 / 1m", 2.0),
        // SI base units with engineering prefixes
        Arguments.of("2km * 3m", 2.0 * 3 * 1000.0),
        Arguments.of("1km * 1Mm", 1.0 * 1 * 1000.0),
        Arguments.of("1km / 1m", 1.0 / 1 * 1000.0),
        Arguments.of("1m / 1km", 1.0 / 1 / 1000.0),
        // prefix operators
        Arguments.of("-(5km)", -5.0),
        Arguments.of("-5km", -5.0),
        Arguments.of("+(5km)", 5.0),
        Arguments.of("(5km)", 5.0)
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
        Arguments.of("1mm > 0m", true),
        Arguments.of("1km/h < 1m/s", true)
    );
  }

  static protected Stream<Arguments> getDeclarationCases() {
    return Stream.of(
        Arguments.of("[km]<double> x = 2km; x", 2.0),
        Arguments.of("[km]<double> x = 1500m; x", 1500 / 1000.0),
        Arguments.of("[m]<double> x = 1km; x", 1 * 1000.0),
        Arguments.of("[km]<double> x = 2km; [m]<double> y = x; y", 2000.0),
        Arguments.of("[km]<double> x = 2km; x + 1000m", 2 + 1000 / 1000.0),
        Arguments.of("[km]<float> x = 2.0f km; [m]<double> y = x; y", 2000.0),
        Arguments.of("[km/h]<double> x = 36km/h; [m/s]<double> y = x; y",
            36 / 3.6),
        // casts
        Arguments.of("([m]<double>) 2km", 2000.0),
        Arguments.of("([m]<int>) 1.5km", 1500),
        Arguments.of("[m]<int> x = ([m]<int>) 1km; x += 1.5km; x", 2500)
    );
  }

  static protected Stream<Arguments> getAssignmentCases() {
    return Stream.of(
        Arguments.of("[m]<double> x = 1m; x = 2km; x", 2000.0),
        Arguments.of("[km]<double> x = 1km; x = 2000m; x", 2.0),
        Arguments.of("[km]<double> x = 1km; x = 2000m", 2.0),
        Arguments.of("[m]<double> x = 1000m; x += 1km; x", 2000.0),
        Arguments.of("[km]<double> x = 1km; x += 500m; x", 1.5),
        Arguments.of("[km]<double> x = 3km; x -= 1000m; x", 2.0),
        Arguments.of("[km]<double> x = 3km; x *= 2; x", 6.0),
        Arguments.of("[km]<double> x = 6km; x /= 4; x", 1.5),
        Arguments.of("[km]<double> x = 7km; x %= 3km; x", 1.0),
        Arguments.of("[km]<double> x = 1km; [m]<double> y = x = 3km; y",
            3000.0),
        Arguments.of("[km]<double> x = 0km; "
            + "for (int i = 0; i < 3; i++) x += 1000m; x", 3.0),
        Arguments.of("int n = 0; "
            + "for ([km]<double> x = 0km; x < 3000m; x += 1000m) n++; n", 3)
    );
  }

  static protected Stream<Arguments> getConditionalCases() {
    return Stream.of(
        // the larger unit
        Arguments.of("true ? 1km : 1m", 1.0),
        Arguments.of("false ? 1km : 1m", 1 / 1000.0),
        Arguments.of("[m]<double> x = true ? 1km : 1m; x", 1000.0)
    );
  }

  static protected Stream<Arguments> getFunctionCases() {
    return Stream.of(
        Arguments.of("[km]<double> -> [km]<double> f = ([km]<double> x) -> x; "
            + "[km]<double> r = f(1000m); r", 1.0),
        Arguments.of("[km]<double> -> [km]<double> f = ([km]<double> x) -> x; "
            + "f(1000m) == 1km", true),
        Arguments.of("[km]<double> -> [m]<double> f = ([km]<double> x) -> x; "
            + "f(2km)", 2000.0),
        Arguments.of("[km]<double> -> [m]<double> f = "
            + "([km]<double> x) -> x + 0m; f(2km)", 2000.0)
    );
  }

  static protected Stream<Arguments> getAccessCases() {
    return Stream.of(
        Arguments.of("[km]<double>[] a = new [km]<double>[1]; "
            + "a[0] = 2km; a[0]", 2.0),
        Arguments.of("[km]<double>[] a = new [km]<double>[1]; "
            + "a[0] = 2km; a[0] + 1000m", 3.0),
        Arguments.of("[km]<double>[] a = new [km]<double>[1]; "
            + "a[0] = 2000m; [m]<double> x = a[0]; x", 2000.0)
    );
  }

  static protected Stream<Arguments> getTupleCases() {
    return Stream.of(
        Arguments.of("(1km, 2)", Tuple2.of(1.0, 2)),
        Arguments.of("(1km, 2m)", Tuple2.of(1.0, 2.0)),
        Arguments.of("(1km, 2)[0]", 1.0),
        Arguments.of("(1km, 2m)[1]", 2.0),
        Arguments.of("([km]<double>, int) t = (1km, 2); t[0]", 1.0),
        Arguments.of("([km]<double>, int) t = (1km, 2); t[0] + 1000m", 2.0),
        Arguments.of("([km]<double>, int) t = (1500m, 2); "
            + "[km]<double> x = t[0]; x", 1.5),
        Arguments.of("([m]<double>, int) t = (1km, 2); t",
            Tuple2.of(1000.0, 2)),
        Arguments.of("([m]<double>, int) t = (1km, 2); t[0]", 1000.0),
        Arguments.of("([km]<double>, [s]<double>) t = (1500m, 2min); t",
            Tuple2.of(1.5, 120.0)),
        Arguments.of("([km]<double>, int) t = (1km, 2); "
            + "([m]<double>, int) u = t; u", Tuple2.of(1000.0, 2)),
        Arguments.of("([m]<double>, int) t = (1m, 2); t = (2km, 3); t",
            Tuple2.of(2000.0, 3)),
        Arguments.of("(([km]<double>, int), int) t = ((1km, 2), 3); t[0][0]",
            1.0),
        Arguments.of("(([km]<double>, int), int) t = ((1km, 2), 3); "
            + "(([m]<double>, int), int) u = t; u",
            Tuple2.of(Tuple2.of(1000.0, 2), 3)),
        Arguments.of("(1km, 2) == (1000m, 2)", true),
        Arguments.of("(1km, 2) == (1m, 2)", false),
        Arguments.of("[km]<double> -> ([m]<double>, int) f = "
            + "([km]<double> x) -> (x, 1); f(2km)", Tuple2.of(2000.0, 1)),
        Arguments.of("(([km]<double>, int)) -> [m]<double> f = "
            + "(([km]<double>, int) t) -> t[0] + 0m; f((3000m, 1))", 3000.0)
    );
  }

  static protected Stream<Arguments> getUnionCases() {
    return Stream.of(
        Arguments.of("([km]<double> | [km]<double>) x = 2km; x", 2.0),
        Arguments.of("([km]<double> | [s]<double>) x = 1km; x", 1.0),
        Arguments.of("([km]<double> | [s]<double>) x = 1min; x", 60.0),
        Arguments.of("([m]<double> | String) x = 1km; x", 1000.0),
        Arguments.of("([km]<double> | String) x = \"a\"; x", "a"),
        Arguments.of("(([km]<double> | [s]<double>), int) t = (1km, 2); t",
            Tuple2.of(1.0, 2))
    );
  }

}
