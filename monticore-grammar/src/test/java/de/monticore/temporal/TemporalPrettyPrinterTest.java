/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.monticore.runtime.junit.PrettyPrinterTester.testPrettyPrinter;

/**
 * Round-trip tests: parse, print, and parse again. The second AST must equal the first one.
 * ISO literals must also be printed as written; German literals are compared as ASTs only.
 */
@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class TemporalPrettyPrinterTest {

  protected static Stream<String> isoInputs() {
    return Stream.of(
        TemporalTestCases.calendarDates(), TemporalTestCases.ordinalDates(),
        TemporalTestCases.weekDates(), TemporalTestCases.isoTimes(),
        TemporalTestCases.isoTimeShifts(), TemporalTestCases.calendarDateTimes(),
        TemporalTestCases.ordinalDateTimes(), TemporalTestCases.weekDateTimes(),
        TemporalTestCases.fullPeriods(), TemporalTestCases.weekPeriods()
    ).flatMap(TemporalPrettyPrinterTest::inputs).distinct();
  }

  protected static Stream<String> germanInputs() {
    return Stream.of(
        TemporalTestCases.germanDates(), TemporalTestCases.germanTimes(),
        TemporalTestCases.germanDateTimes()
    ).flatMap(TemporalPrettyPrinterTest::inputs).distinct();
  }

  static Stream<String> inputs(Stream<Arguments> testCases) {
    return testCases.map(arguments -> (String) arguments.get()[0]);
  }

  @DisplayName("ISO Round Trip")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("isoInputs")
  void testIsoRoundTrip(String input) {
    CombinedTemporalsTestParser parser = CombinedTemporalsTestMill.parser();
    testPrettyPrinter(input, parser, parser::parse_String,
        ast -> CombinedTemporalsTestMill.prettyPrint(ast, false),
        printed -> input.equals(printed.trim()));
  }

  @DisplayName("German Round Trip")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("germanInputs")
  void testGermanRoundTrip(String input) {
    CombinedTemporalsTestParser parser = CombinedTemporalsTestMill.parser();
    testPrettyPrinter(input, parser, parser::parse_String,
        ast -> CombinedTemporalsTestMill.prettyPrint(ast, false));
  }
}
