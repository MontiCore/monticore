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
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;

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
      int year, int month, int day) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  void testOrdinalDates(String input, int year, int dayOfYear) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTOrdinalDate.class, ast.getInstant());
    ASTOrdinalDate realAst = (ASTOrdinalDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getYear());
    Assertions.assertEquals(dayOfYear, realAst.getDayOfYear());
  }
  
  @DisplayName("Week Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDates")
  public void testWeekDateParsing(String input, int year, int week, int dayOfWeek) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTWeekDate.class, ast.getInstant());
    ASTWeekDate realAst = (ASTWeekDate) ast.getInstant();
    
    Assertions.assertEquals(year, realAst.getYear());
    Assertions.assertEquals(week, realAst.getWeek());
    Assertions.assertEquals(dayOfWeek, realAst.getDayOfWeek());
  }
  
  @DisplayName("ISO Time Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#isoTimes")
  public void testIsoTimeParsing(String input, int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());
    ASTISOTime realAst = (ASTISOTime) ast.getInstant();
    
    Assertions.assertEquals(hour, realAst.getHour());
    Assertions.assertEquals(minute, realAst.isPresentMinute() ? realAst.getMinute() : -1);
    Assertions.assertEquals(second, realAst.isPresentSecond() ? realAst.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, realAst.isPresentDecimalDigits() ? realAst.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, realAst.isPresentTimeShift() ? Optional.of(realAst.getTimeShift()) : Optional.empty());
  }
  
  @DisplayName("Calendar DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDateTimes")
  public void testCalendarDateTimeParsing(String input, int year, int month, int day,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
    Assertions.assertEquals(timeShift, time.isPresentTimeShift() ? Optional.of(time.getTimeShift()) : Optional.empty());
  }
  
  @DisplayName("Ordinal DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#ordinalDateTimes")
  public void testOrdinalDateTimeParsing(String input, int year, int dayOfYear,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
    Assertions.assertEquals(timeShift, time.isPresentTimeShift() ? Optional.of(time.getTimeShift()) : Optional.empty());
  }
  
  @DisplayName("Week DateTime Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDateTimes")
  public void testWeekDateTimeParsing(String input, int year, int week, int dayOfWeek,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());
    ASTISODateTime realAst = (ASTISODateTime) ast.getInstant();
    
    Assertions.assertNotNull(realAst.getDate());
    Assertions.assertInstanceOf(ASTWeekDate.class, realAst.getDate());
    Assertions.assertNotNull(realAst.getTime());
    ASTWeekDate date = (ASTWeekDate) realAst.getDate();
    ASTISOTime time = realAst.getTime();
    
    Assertions.assertEquals(year, date.getYear());
    Assertions.assertEquals(week, date.getWeek());
    Assertions.assertEquals(dayOfWeek, date.getDayOfWeek());
    Assertions.assertEquals(hour, time.getHour());
    Assertions.assertEquals(minute, time.isPresentMinute() ? time.getMinute() : -1);
    Assertions.assertEquals(second, time.isPresentSecond() ? time.getSecond() : -1);
    Assertions.assertEquals(decimalDigits, time.isPresentDecimalDigits() ? time.getDecimalDigits() : "");
    Assertions.assertEquals(timeShift, time.isPresentTimeShift() ? Optional.of(time.getTimeShift()) : Optional.empty());
  }
  
  @DisplayName("Full Period Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#fullPeriods")
  void testFullPeriodParsing(String input, int years, int months, int days, int hours,
      int minutes, int seconds, String decimalDigits) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  void testWeekPeriodParsing(String input, int weeks) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  void testFullPeriodInvalidParse(String input) {
    Optional<ASTTemporal>
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input));
    
    MCAssertions.assertHasFindings(Finding::isError);
  }
  
  @DisplayName("Numeric Date Parsing")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#numericDatesWithoutYear")
  public void testNumericDateParsing(String input, int year, int month, int day) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  public void testAlphanumericDateParsing(String input, int year, int month, int day) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  public void testGermanTimeParsing(String input, int hour, int minute, int second) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
  public void testGermanDateTimeParsing(String input, int year, int month, int day, int hour, int minute, int second) {
    ASTTemporal
        ast = Assertions.assertDoesNotThrow(() -> parser.parse_String(input).orElseThrow());
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
}
