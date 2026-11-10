/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.combinedtemporalstest._cocos.CombinedTemporalsTestCoCoChecker;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import de.monticore.temporal.temporalbasis.cocos.DateFieldsInRangeCoCo;
import de.monticore.temporal.temporalbasis.cocos.TimeFieldsInRangeCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class TemporalRangeCoCoTest {
  CombinedTemporalsTestParser parser;

  CombinedTemporalsTestCoCoChecker checker;

  @BeforeEach
  void setup() {
    parser = CombinedTemporalsTestMill.parser();
    checker = new CombinedTemporalsTestCoCoChecker();
    checker.addCoCo(new DateFieldsInRangeCoCo());
    checker.addCoCo(new TimeFieldsInRangeCoCo());
  }

  protected static Stream<String> validInstants() {
    return Stream.of(
        TemporalTestCases.calendarDates(), TemporalTestCases.ordinalDates(),
        TemporalTestCases.weekDates(), TemporalTestCases.isoTimes(),
        TemporalTestCases.calendarDateTimes(), TemporalTestCases.ordinalDateTimes(),
        TemporalTestCases.weekDateTimes(), TemporalTestCases.germanDates(),
        TemporalTestCases.germanTimes(), TemporalTestCases.germanDateTimes()
    ).flatMap(testCases -> testCases.map(arguments -> (String) arguments.get()[0]));
  }

  @DisplayName("Valid Instants")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("validInstants")
  void testValidInstants(String input) throws IOException {
    checkValid(input);
  }

  // Boundaries; 24:00 and 23:59:60 are accepted until their semantics is decided
  @DisplayName("Valid Boundaries")
  @ParameterizedTest(name = "Input: {0}")
  @ValueSource(strings = {"2016-02-29", "2017-02-28", "2017-12-31", "2016-366", "2017-365",
      "2017-001", "29.02.2016", "29. Februar 2016", "31. Dez. 2017", "00:00:00", "23:59:59",
      "24:00", "23:59:60", "0 Uhr", "23:59 Uhr"})
  void testValidBoundaries(String input) throws IOException {
    checkValid(input);
  }

  protected static Stream<Arguments> invalidInstants() {
    return Stream.of(
        // ISO dates
        Arguments.of("2017-13-04", DateFieldsInRangeCoCo.MONTH_ERROR_CODE),
        Arguments.of("2017-00-04", DateFieldsInRangeCoCo.MONTH_ERROR_CODE),
        Arguments.of("2017-12-32", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        Arguments.of("2017-12-00", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        Arguments.of("2017-02-29", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        Arguments.of("2017-04-31", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        Arguments.of("2017-366", DateFieldsInRangeCoCo.DAY_OF_YEAR_ERROR_CODE),
        Arguments.of("2016-367", DateFieldsInRangeCoCo.DAY_OF_YEAR_ERROR_CODE),
        Arguments.of("2017-000", DateFieldsInRangeCoCo.DAY_OF_YEAR_ERROR_CODE),
        // German dates
        Arguments.of("04.13.2017", DateFieldsInRangeCoCo.MONTH_ERROR_CODE),
        Arguments.of("32.01.2017", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        Arguments.of("30. Februar 2017", DateFieldsInRangeCoCo.DAY_OF_MONTH_ERROR_CODE),
        // ISO times
        Arguments.of("25:00", TimeFieldsInRangeCoCo.HOUR_ERROR_CODE),
        Arguments.of("T2500", TimeFieldsInRangeCoCo.HOUR_ERROR_CODE),
        Arguments.of("12:60", TimeFieldsInRangeCoCo.MINUTE_ERROR_CODE),
        Arguments.of("12:30:61", TimeFieldsInRangeCoCo.SECOND_ERROR_CODE),
        // German times
        Arguments.of("25 Uhr", TimeFieldsInRangeCoCo.HOUR_ERROR_CODE),
        Arguments.of("12:75 Uhr", TimeFieldsInRangeCoCo.MINUTE_ERROR_CODE),
        // date-times
        Arguments.of("2017-13-04T12:30:01", DateFieldsInRangeCoCo.MONTH_ERROR_CODE),
        Arguments.of("2017-12-04T25:30:01", TimeFieldsInRangeCoCo.HOUR_ERROR_CODE),
        Arguments.of("04.13.2017 12:30 Uhr", DateFieldsInRangeCoCo.MONTH_ERROR_CODE),
        Arguments.of("04.12.2017 25 Uhr", TimeFieldsInRangeCoCo.HOUR_ERROR_CODE)
    );
  }

  @DisplayName("Invalid Instants")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("invalidInstants")
  void testInvalidInstants(String input, String errorCode) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();

    checker.checkAll(ast);

    Assertions.assertEquals(1, Log.getFindings().size(), () -> Log.getFindings().toString());
    Finding finding = MCAssertions.assertHasFindingStartingWith(errorCode);
    Assertions.assertTrue(finding.isError());
  }

  void checkValid(String input) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    checker.checkAll(ast);
    MCAssertions.assertNoFindings();
  }
}
