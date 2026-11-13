/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Round-trip tests: parse, print, and parse again. The second AST must equal the first one,
 * including the values set by the second parse step. German literals are compared as ASTs
 * only, since e.g. the abbreviation "Apr." and "April" share one constant.
 */
@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class TemporalPrettyPrinterTest {
  CombinedTemporalsTestParser parser;

  @BeforeEach
  void setup() {
    parser = CombinedTemporalsTestMill.parser();
  }

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
  void testIsoRoundTrip(String input) throws IOException {
    String printed = checkRoundTrip(input);
    Assertions.assertEquals(input, printed.trim());
  }

  @DisplayName("German Round Trip")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("germanInputs")
  void testGermanRoundTrip(String input) throws IOException {
    checkRoundTrip(input);
  }

  String checkRoundTrip(String input) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    String printed = CombinedTemporalsTestMill.prettyPrint(ast, false);
    ASTTemporal reparsed = parser.parse_String(printed)
        .orElseGet(() -> MCAssertions.failAndPrintFindings("Cannot parse the printed '" + printed + "'"));
    Assertions.assertTrue(ast.deepEquals(reparsed), () -> "'" + input + "' printed as '" + printed + "'");
    MCAssertions.assertNoFindings();
    return printed;
  }
}
