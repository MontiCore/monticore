/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.detemporals._ast.ASTDEAlphanumericDate;
import de.monticore.temporal.detemporals._ast.ASTDEDateTime;
import de.monticore.temporal.detemporals._ast.ASTDENumericDate;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.isotemporals._ast.*;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;

@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class CombinedTemporalsTest {
  CombinedTemporalsTestParser parser;
  
  @BeforeEach
  void setup() {
    parser = CombinedTemporalsTestMill.parser();
  }
  
  @DisplayName("Calendar Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDates")
  void testCalendarDates(String input, int century, int decade,
      int year, int month, int day) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTCalendarDate.class, ast.getInstant());
    ASTCalendarDate realAst = (ASTCalendarDate) ast.getInstant();
    
    Assertions.assertEquals(century, realAst.isPresentCentury() ? realAst.getCentury() : -1);
    Assertions.assertEquals(decade, realAst.isPresentDecade() ? realAst.getDecade() : -1);
    Assertions.assertEquals(year, realAst.isPresentYear() ? realAst.getYear() : -1);
    Assertions.assertEquals(month, realAst.isPresentMonth() ? realAst.getMonth() : -1);
    Assertions.assertEquals(day, realAst.isPresentDay() ? realAst.getDay() : -1);
  }
  
  @DisplayName("Ordinal Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#ordinalDates")
  void testOrdinalDates(String input, int year, int dayOfYear) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTOrdinalDate.class, ast.getInstant());
    ASTOrdinalDate realAst = (ASTOrdinalDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getYear());
    Assertions.assertEquals(dayOfYear, realAst.getDayOfYear());
  }
  
  @DisplayName("Week Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDates")
  public void testWeekDateParsing(String input, int year, int week, int dayOfWeek) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTWeekDate.class, ast.getInstant());
    ASTWeekDate realAst = (ASTWeekDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getWeekBasedYear());
    Assertions.assertEquals(week, realAst.getWeekOfWeekBasedYear());
    Assertions.assertEquals(dayOfWeek, realAst.isPresentDayOfWeek() ? realAst.getDayOfWeek() : -1);
  }
  
  @DisplayName("ISO Time Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#isoTimes")
  public void testIsoTimeParsing(String input, int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());
    ASTISOTime realAst = (ASTISOTime) ast.getInstant();
    
    Assertions.assertEquals(hour, realAst.getHour());
    Assertions.assertEquals(minute, realAst.isPresentMinute() ? realAst.getMinute() : -1);
    Assertions.assertEquals(second, realAst.isPresentSecond() ? realAst.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, realAst.isPresentDecimalDigits() ? realAst.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, realAst.isPresentTimeShiftHour() ? Optional.of(realAst.getTimeShiftHour()) : Optional.empty());
  }
  
  @DisplayName("Calendar DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDateTimes")
  public void testCalendarDateTimeParsing(String input, int year, int month, int day,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());
    ASTISODateTime realAst = (ASTISODateTime) ast.getInstant();
    
    Assertions.assertNotNull(realAst.getDate());
    Assertions.assertInstanceOf(ASTCalendarDate.class, realAst.getDate());
    Assertions.assertNotNull(realAst.getTime());
    ASTCalendarDate date = (ASTCalendarDate) realAst.getDate();
    ASTISOTime time = realAst.getTime();
    
    Assertions.assertEquals(year, date.getYear());
    Assertions.assertEquals(month, date.getMonth());
    Assertions.assertEquals(day, date.getDay());
    Assertions.assertEquals(hour, time.getHour());
    Assertions.assertEquals(minute, time.isPresentMinute() ? time.getMinute() : -1);
    Assertions.assertEquals(second, time.isPresentSecond() ? time.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, time.isPresentDecimalDigits() ? time.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, time.isPresentTimeShiftHour() ? Optional.of(time.getTimeShiftHour()) : Optional.empty());
  }
  
  @DisplayName("Ordinal DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#ordinalDateTimes")
  public void testOrdinalDateTimeParsing(String input, int year, int dayOfYear,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());
    ASTISODateTime realAst = (ASTISODateTime) ast.getInstant();
    
    Assertions.assertNotNull(realAst.getDate());
    Assertions.assertInstanceOf(ASTOrdinalDate.class, realAst.getDate());
    Assertions.assertNotNull(realAst.getTime());
    ASTOrdinalDate date = (ASTOrdinalDate) realAst.getDate();
    ASTISOTime time = realAst.getTime();
    
    Assertions.assertEquals(year, date.getYear());
    Assertions.assertEquals(dayOfYear, date.getDayOfYear());
    Assertions.assertEquals(hour, time.getHour());
    Assertions.assertEquals(minute, time.isPresentMinute() ? time.getMinute() : -1);
    Assertions.assertEquals(second, time.isPresentSecond() ? time.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, time.isPresentDecimalDigits() ? time.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, time.isPresentTimeShiftHour() ? Optional.of(time.getTimeShiftHour()) : Optional.empty());
  }
  
  @DisplayName("Week DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDateTimes")
  public void testWeekDateTimeParsing(String input, int year, int week, int dayOfWeek,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());
    ASTISODateTime realAst = (ASTISODateTime) ast.getInstant();
    
    Assertions.assertNotNull(realAst.getDate());
    Assertions.assertInstanceOf(ASTWeekDate.class, realAst.getDate());
    Assertions.assertNotNull(realAst.getTime());
    ASTWeekDate date = (ASTWeekDate) realAst.getDate();
    ASTISOTime time = realAst.getTime();
    
    Assertions.assertEquals(year, date.getWeekBasedYear());
    Assertions.assertEquals(week, date.getWeekOfWeekBasedYear());
    Assertions.assertEquals(dayOfWeek, date.getDayOfWeek());
    Assertions.assertEquals(hour, time.getHour());
    Assertions.assertEquals(minute, time.isPresentMinute() ? time.getMinute() : -1);
    Assertions.assertEquals(second, time.isPresentSecond() ? time.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, time.isPresentDecimalDigits() ? time.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, time.isPresentTimeShiftHour() ? Optional.of(time.getTimeShiftHour()) : Optional.empty());
  }
  
  @DisplayName("Full Period Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#fullPeriods")
  void testFullPeriodParsing(String input, int years, int months, int days, int hours,
      int minutes, int seconds, String decimalDigits) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentPeriod());
    Assertions.assertInstanceOf(ASTFullPeriod.class, ast.getPeriod());
    ASTFullPeriod realAst = (ASTFullPeriod) ast.getPeriod();
    
    Assertions.assertEquals(input, realAst.toRawString());
    Assertions.assertEquals(years, years == 0 ? 0 : realAst.getYears());
    Assertions.assertEquals(months, months == 0 ? 0 : realAst.getMonths());
    Assertions.assertEquals(days, days == 0 ? 0 : realAst.getDays());
    Assertions.assertEquals(hours, hours == 0 ? 0 : realAst.getHours());
    Assertions.assertEquals(minutes, minutes == 0 ? 0 : realAst.getMinutes());
    Assertions.assertEquals(seconds, seconds == 0 ? 0 : realAst.getSeconds());
    Assertions.assertEquals(decimalDigits, realAst.isPresentDecimalDigits() ? realAst.getDecimalDigits() : "");
  }
  
  @DisplayName("Week Period Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekPeriods")
  void testWeekPeriodParsing(String input, int weeks) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentPeriod());
    Assertions.assertInstanceOf(ASTWeekPeriod.class, ast.getPeriod());
    ASTWeekPeriod realAst = (ASTWeekPeriod) ast.getPeriod();
    
    Assertions.assertEquals(input, realAst.toRawString());
    Assertions.assertEquals(weeks, realAst.getWeeks());
  }
  
  @SuppressWarnings("unused")
  @DisplayName("Full Period Parse Invalid")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#invalidPeriods")
  void testFullPeriodInvalidParse(String input) throws IOException {
    Optional<ASTTemporal> ast = parser.parse_String(input);
    
    MCAssertions.assertHasFindings(Finding::isError);
  }

  @DisplayName("Full Period Second Parse Step Invalid")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#periodsRejectedBySecondStep")
  void testFullPeriodSecondParseStepInvalid(String input) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(ASTFullPeriod.class, ast.getPeriod());
    
    Finding finding = MCAssertions.assertHasFindingStartingWith(ISOTemporals2ndParser.FULL_PERIOD_ERROR_CODE);
    Assertions.assertTrue(finding.isError());
  }
  
  // These inputs pass the first parse step but are rejected by the second one.
  // The second parser reports exactly one error at the position of the date instead of
  // throwing an exception. The field values of the AST are meaningless in that case.
  @DisplayName("Basic Week Date Parse Invalid")
  @ParameterizedTest(name = "Input: {0}")
  @ValueSource(strings = {"3W4", "2017W4", "17W481"})
  void testBasicWeekDateInvalidParse(String input) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(ASTBasicWeekDate.class, ast.getInstant());

    Assertions.assertEquals(1, Log.getFindings().size());
    Finding finding = MCAssertions.assertHasFindingStartingWith(ISOTemporals2ndParser.WEEK_DATE_ERROR_CODE);
    Assertions.assertTrue(finding.isError());
    Assertions.assertEquals(1, finding.getSourcePosition().map(SourcePosition::getLine).orElse(0));
    Assertions.assertEquals(0, finding.getSourcePosition().map(SourcePosition::getColumn).orElse(-1));
  }

  // Inputs for the other constructs that pass the first parse step but not the second one.
  @DisplayName("Second Parse Step Invalid")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("secondParseStepFailures")
  void testSecondParseStepInvalid(String input, Class<?> astClass, String errorCode,
      String expected) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(astClass, ast.getInstant());
    
    Assertions.assertEquals(1, Log.getFindings().size());
    Finding finding = MCAssertions.assertHasFindingStartingWith(errorCode);
    Assertions.assertTrue(finding.isError());
    Assertions.assertTrue(finding.getMsg().contains("Expected " + expected + " but got '" + input + "'"),
        finding.getMsg());
    Assertions.assertEquals(1, finding.getSourcePosition().map(SourcePosition::getLine).orElse(0));
  }
  
  static Stream<Arguments> secondParseStepFailures() {
    return Stream.of(
        Arguments.of("-0002-04-12", ASTCalendarDate.class,
            ISOTemporals2ndParser.CALENDAR_DATE_ERROR_CODE, "a calendar date"),
        Arguments.of("2017-1", ASTCalendarDate.class,
            ISOTemporals2ndParser.CALENDAR_DATE_ERROR_CODE, "a calendar date"),
        Arguments.of("12:30:01-0", ASTISOTime.class,
            ISOTemporals2ndParser.ISO_TIME_ERROR_CODE, "an ISO time"),
        Arguments.of("1:2", ASTISOTime.class,
            ISOTemporals2ndParser.ISO_TIME_ERROR_CODE, "an ISO time"),
        Arguments.of("T1:2:3", ASTISOTime.class,
            ISOTemporals2ndParser.ISO_TIME_ERROR_CODE, "an ISO time"),
        Arguments.of("17W481T1230", ASTISODateTime.class,
            ISOTemporals2ndParser.ISO_DATE_TIME_ERROR_CODE, "an ISO date-time")
    );
  }

  // The date and the time of a date-time are built from a single token; each gets the
  // position of its part of that token, e.g. for reporting CoCo errors.
  @DisplayName("ISO Date-Time Part Positions")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("dateTimePartPositions")
  void testISODateTimePartPositions(String input, int line, int dateStart, int dateEnd,
      int timeStart, int timeEnd) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ASTISODateTime dateTime = Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());

    assertPosition(line, dateStart, dateEnd, dateTime.getDate());
    assertPosition(line, timeStart, timeEnd, dateTime.getTime());
  }

  static Stream<Arguments> dateTimePartPositions() {
    return Stream.of(
        Arguments.of("2017-12-04T12:30", 1, 0, 10, 10, 16),
        Arguments.of("  2017-12-04T12:30", 1, 2, 12, 12, 18),
        Arguments.of("\n2017-12-04T12:30", 2, 0, 10, 10, 16),
        Arguments.of("+002017-12-04T12:30:15.5-05:30", 1, 0, 13, 13, 30),
        Arguments.of("2017-338T12Z", 1, 0, 8, 8, 12),
        Arguments.of("2017W481T1230", 1, 0, 8, 8, 13)
    );
  }

  protected void assertPosition(int line, int start, int end, ASTISOTemporalsNode node) {
    // SourcePosition does not implement equals
    Assertions.assertEquals(line + ":" + start, toString(node.get_SourcePositionStart()));
    Assertions.assertEquals(line + ":" + end, toString(node.get_SourcePositionEnd()));
  }

  protected String toString(SourcePosition position) {
    return position.getLine() + ":" + position.getColumn();
  }

  @DisplayName("Numeric Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#numericDatesWithoutYear")
  public void testNumericDateParsing(String input, int year, int month, int day) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDENumericDate.class, ast.getInstant());
    ASTDENumericDate realAst = (ASTDENumericDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getYear());
    Assertions.assertEquals(month, realAst.isPresentMonth() ? realAst.getMonth() : -1);
    Assertions.assertEquals(day, realAst.isPresentDay() ? realAst.getDay() : -1);
  }
  
  @DisplayName("Alphanumeric Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#alphanumericDates")
  public void testAlphanumericDateParsing(String input, int year, int month, int day) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDEAlphanumericDate.class, ast.getInstant());
    ASTDEAlphanumericDate realAst = (ASTDEAlphanumericDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getYear());
    Assertions.assertEquals(month, realAst.getMonth());
    Assertions.assertEquals(day, realAst.isPresentDay() ? realAst.getDay() : -1);
  }
  
  @DisplayName("German Time Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanTimes")
  public void testGermanTimeParsing(String input, int hour, int minute, int second) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDETime.class, ast.getInstant());
    ASTDETime realAst = (ASTDETime) ast.getInstant();
    
    Assertions.assertEquals(hour, realAst.getHour());
    Assertions.assertEquals(minute, realAst.isPresentMinute() ? realAst.getMinute() : -1);
    Assertions.assertEquals(second, realAst.isPresentSecond() ? realAst.getSecond() : -1);
  }
  
  @DisplayName("German DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanDateTimes")
  public void testGermanDateTimeParsing(String input, int year, int month, int day, int hour, int minute, int second) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDEDateTime.class, ast.getInstant());
    ASTDEDateTime realAst = (ASTDEDateTime) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getDate().getYear());
    Assertions.assertEquals(month, realAst.getDate().getMonth());
    Assertions.assertEquals(day, realAst.getDate().getDay());
    Assertions.assertEquals(hour, realAst.getTime().getHour());
    Assertions.assertEquals(minute, realAst.getTime().isPresentMinute() ? realAst.getTime().getMinute() : -1);
    Assertions.assertEquals(second, realAst.getTime().isPresentSecond() ? realAst.getTime().getSecond() : -1);
  }

  // Times without seconds are no OffsetTime, so hour, minute and offset are compared directly.
  // 18:30-08:00 is the example in Temporal.md.
  @DisplayName("ISO Time without Seconds with Time Shift")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("timesWithoutSecondsWithTimeShift")
  void testTimeWithoutSecondsWithTimeShift(String input, LocalTime expectedTime, ZoneOffset offset)
      throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());

    Assertions.assertEquals(expectedTime.getHour(), time.get(HOUR_OF_DAY));
    Assertions.assertEquals(expectedTime.getMinute(), time.get(MINUTE_OF_HOUR));
    Assertions.assertFalse(time.isSupported(SECOND_OF_MINUTE));
    Assertions.assertEquals(offset.getTotalSeconds(), time.get(OFFSET_SECONDS));
  }

  static Stream<Arguments> timesWithoutSecondsWithTimeShift() {
    return Stream.of(
        Arguments.of("18:30-08:00", LocalTime.of(18, 30), ZoneOffset.ofHours(-8)),
        Arguments.of("12:00-05:30", LocalTime.of(12, 0), ZoneOffset.ofHoursMinutes(-5, -30)),
        Arguments.of("T1200-0530", LocalTime.of(12, 0), ZoneOffset.ofHoursMinutes(-5, -30))
    );
  }

  // A date-time needs the designator T between date and time (ISO 8601-1, 5.4.2).
  // Without it, the input is no date-time and the parser rejects it.
  @DisplayName("ISO Date-Time without T")
  @ParameterizedTest(name = "Input: {0}")
  @ValueSource(strings = {"2017-12-0412:30", "2017-12-0412:30:01-05:00", "20171204123001-05:00"})
  void testDateTimeWithoutT(String input) throws IOException {
    Optional<ASTTemporal> ast = parser.parse_String(input);

    Assertions.assertTrue(ast.isEmpty());
    MCAssertions.assertHasFindings(Finding::isError);
  }
}
