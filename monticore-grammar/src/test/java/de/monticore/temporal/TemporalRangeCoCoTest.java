/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.combinedtemporalstest._cocos.CombinedTemporalsTestCoCoChecker;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import de.monticore.temporal.detemporals.cocos.DETemporalsCoCos;
import de.monticore.temporal.detemporals.cocos.DEYearHasFourDigitsCoCo;
import de.monticore.temporal.isotemporals.cocos.ISOTemporalsCoCos;
import de.monticore.temporal.isotemporals.cocos.ISOTimeFieldsInRangeCoCo;
import de.monticore.temporal.isotemporals.cocos.WeekDateFieldsInRangeCoCo;
import de.monticore.temporal.temporalbasis.cocos.DateFieldsInRangeCoCo;
import de.monticore.temporal.temporalbasis.cocos.TemporalBasisCoCos;
import de.monticore.temporal.temporalbasis.cocos.TimeFieldsInRangeCoCo;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    checker.addChecker(new TemporalBasisCoCos().getCoCoChecker());
    checker.addChecker(new ISOTemporalsCoCos().getCoCoChecker());
    checker.addChecker(new DETemporalsCoCos().getCoCoChecker());
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

  // Boundaries, including the end of the day (24:00) and leap seconds (23:59:60 UTC); a leap
  // second without time shift is accepted in any minute, as the offset to UTC is unknown
  @DisplayName("Valid Boundaries")
  @ParameterizedTest(name = "Input: {0}")
  @ValueSource(strings = {"2016-02-29", "2017-02-28", "2017-12-31", "2016-366", "2017-365",
      "2017-001", "29.02.2016", "29. Februar 2016", "31. Dez. 2017", "00:00:00", "23:59:59",
      "24:00", "24:00:00", "T2400", "24:00:00,000", "2017-12-04T24:00", "24 Uhr", "24:00 Uhr",
      "23:59:60Z", "00:59:60+01:00", "05:29:60+05:30", "23:59:60", "12:30:60", "0 Uhr", "23:59 Uhr",
      "12:00+18:00", "12:00-18", "2015-W53-7", "2020-W53", "2017-W52-7", "2017-W01-1", "2017W017",
      "04.12.0017"})
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
        // end of the day
        Arguments.of("24:30", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("24:00:01", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("24:00:60", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("24:00:00,5", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("24:00,5", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("T24,5", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("24:30 Uhr", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        Arguments.of("2017-12-04T24:01", TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
        // leap seconds
        Arguments.of("12:30:60Z", TimeFieldsInRangeCoCo.LEAP_SECOND_ERROR_CODE),
        Arguments.of("00:00:60Z", TimeFieldsInRangeCoCo.LEAP_SECOND_ERROR_CODE),
        Arguments.of("23:59:60+01:00", TimeFieldsInRangeCoCo.LEAP_SECOND_ERROR_CODE),
        Arguments.of("2016-12-31T12:59:60Z", TimeFieldsInRangeCoCo.LEAP_SECOND_ERROR_CODE),
        // time shifts
        Arguments.of("12:00+19:00", TimeFieldsInRangeCoCo.TIME_SHIFT_ERROR_CODE),
        Arguments.of("12:00-99", TimeFieldsInRangeCoCo.TIME_SHIFT_ERROR_CODE),
        Arguments.of("2017-12-04T12:00+25:00", TimeFieldsInRangeCoCo.TIME_SHIFT_ERROR_CODE),
        Arguments.of("12:00+05:75", ISOTimeFieldsInRangeCoCo.TIME_SHIFT_MINUTE_ERROR_CODE),
        Arguments.of("12:00+05:60", ISOTimeFieldsInRangeCoCo.TIME_SHIFT_MINUTE_ERROR_CODE),
        // week dates
        Arguments.of("2017-W53-1", WeekDateFieldsInRangeCoCo.WEEK_ERROR_CODE),
        Arguments.of("2017-W54", WeekDateFieldsInRangeCoCo.WEEK_ERROR_CODE),
        Arguments.of("2017-W00", WeekDateFieldsInRangeCoCo.WEEK_ERROR_CODE),
        Arguments.of("2017W531", WeekDateFieldsInRangeCoCo.WEEK_ERROR_CODE),
        Arguments.of("2017-W01-8", WeekDateFieldsInRangeCoCo.DAY_OF_WEEK_ERROR_CODE),
        Arguments.of("2017-W01-0", WeekDateFieldsInRangeCoCo.DAY_OF_WEEK_ERROR_CODE),
        Arguments.of("2017W018", WeekDateFieldsInRangeCoCo.DAY_OF_WEEK_ERROR_CODE),
        Arguments.of("2017-W53-1T12:00", WeekDateFieldsInRangeCoCo.WEEK_ERROR_CODE),
        // German years
        Arguments.of("04.12.17", DEYearHasFourDigitsCoCo.ERROR_CODE),
        Arguments.of("12.17", DEYearHasFourDigitsCoCo.ERROR_CODE),
        Arguments.of("4. Dez. 17", DEYearHasFourDigitsCoCo.ERROR_CODE),
        Arguments.of("04.12.02017", DEYearHasFourDigitsCoCo.ERROR_CODE),
        Arguments.of("04.12.17 12 Uhr", DEYearHasFourDigitsCoCo.ERROR_CODE),
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

  // The date and time of an ISO date-time are converted from a single token, but are reported at it
  @Test
  void testDateTimeFindingPosition() throws IOException {
    ASTTemporal ast = parser.parse_String("2017-13-04T25:30:01")
        .orElseGet(MCAssertions::failAndPrintFindings);

    checker.checkAll(ast);

    Assertions.assertEquals(2, Log.getFindings().size(), () -> Log.getFindings().toString());
    for (Finding finding : Log.getFindings()) {
      Assertions.assertEquals(ast.get_SourcePositionStart(), finding.getSourcePosition().orElseThrow());
    }
    Log.clearFindings();
  }

  void checkValid(String input) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    checker.checkAll(ast);
    MCAssertions.assertNoFindings();
  }
}
