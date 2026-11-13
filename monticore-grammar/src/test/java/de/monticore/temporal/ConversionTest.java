/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.isotemporals._ast.ASTBasicWeekDate;
import de.monticore.temporal.isotemporals._ast.ASTCalendarDate;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.monticore.temporal.isotemporals._ast.ASTWeekDate;
import de.monticore.temporal.isotemporals.parsing.ISOTemporals2ndParser;
import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._ast.ASTDateTime;
import de.monticore.temporal.temporalbasis._ast.ASTInstant;
import de.monticore.temporal.temporalbasis._ast.ASTTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.IsoFields;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;

@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class ConversionTest {
  
  @SuppressWarnings("unused") // Some parameters are unused, but JUnit demands them
  @DisplayName("Calendar Date Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDates")
  void testCalendarDateConversion(String input, int century, int decade,
      int year, int month, int day) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());
    ASTDate realAst = (ASTDate) ast.getInstant();
    
    if (year == -1) {
      Assertions.assertFalse(realAst.isSupported(YEAR));
    } else {
      Assertions.assertTrue(realAst.isSupported(YEAR));
      Assertions.assertEquals(year, realAst.get(YEAR));
    }
    
    if (month == -1) {
      Assertions.assertFalse(realAst.isSupported(MONTH_OF_YEAR));
    } else {
      Assertions.assertTrue(realAst.isSupported(MONTH_OF_YEAR));
      Assertions.assertEquals(month, realAst.get(MONTH_OF_YEAR));
    }
    
    if (day == -1) {
      Assertions.assertFalse(realAst.isSupported(DAY_OF_MONTH));
    } else {
      Assertions.assertTrue(realAst.isSupported(DAY_OF_MONTH));
      Assertions.assertEquals(day, realAst.get(DAY_OF_MONTH));
    }
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
          realAst.isExactlyYear(),
          realAst.isExactlyYearMonth(),
          realAst.isLocalDate(),
          ((ASTCalendarDate) realAst).isPresentDecade(),
          ((ASTCalendarDate) realAst).isPresentCentury()
        )
        .filter(i -> i).count());
    if (realAst.isExactlyYear()) {
      Assertions.assertEquals(Year.of(year), realAst.toYear());
    }
    if (realAst.isExactlyYearMonth()) {
      Assertions.assertEquals(YearMonth.of(year, month), realAst.toYearMonth());
    }
    if (realAst.isLocalDate()) {
      Assertions.assertEquals(LocalDate.of(year, month, day), realAst.toLocalDate());
    }
  }
  
  @DisplayName("Ordinal Date Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#ordinalDates")
  void testOrdinalDateConversion(String input, int year, int dayOfYear) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());
    ASTDate realAst = (ASTDate) ast.getInstant();
    
    Assertions.assertTrue(realAst.isSupported(YEAR));
    Assertions.assertEquals(year, realAst.get(YEAR));
    
    Assertions.assertTrue(realAst.isSupported(DAY_OF_YEAR));
    Assertions.assertEquals(dayOfYear, realAst.get(DAY_OF_YEAR));
    
    Assertions.assertTrue(realAst.isLocalDate());
    if (realAst.isLocalDate()) {
      Assertions.assertEquals(LocalDate.ofYearDay(year, dayOfYear), realAst.toLocalDate());
    }
  }
  
  @DisplayName("Week Date Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDates")
  public void testWeekDateConversion(String input, int year, int week, int dayOfWeek) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());
    ASTDate realAst = (ASTDate) ast.getInstant();
    
    Assertions.assertTrue(realAst.isSupported(IsoFields.WEEK_BASED_YEAR));
    Assertions.assertEquals(year, realAst.get(IsoFields.WEEK_BASED_YEAR));
    
    Assertions.assertTrue(realAst.isSupported(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    Assertions.assertEquals(week, realAst.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    
    if (dayOfWeek == -1) {
      Assertions.assertFalse(realAst.isSupported(DAY_OF_WEEK));
      Assertions.assertThrows(UnsupportedTemporalTypeException.class, () -> realAst.getLong(DAY_OF_WEEK));
      Assertions.assertFalse(realAst.isSupported(YEAR));
      Assertions.assertFalse(realAst.isSupported(ASTWeekDate.WEEK_OF_YEAR));
      Assertions.assertThrows(UnsupportedTemporalTypeException.class, () -> realAst.getLong(ASTWeekDate.WEEK_OF_YEAR));
      Assertions.assertThrows(UnsupportedTemporalTypeException.class, ((ASTWeekDate) realAst)::getCalendarYear);
    } else {
      Assertions.assertTrue(realAst.isSupported(DAY_OF_WEEK));
      Assertions.assertEquals(dayOfWeek, realAst.get(DAY_OF_WEEK));
      Assertions.assertTrue(realAst.isSupported(YEAR));
      Assertions.assertTrue(realAst.isSupported(ASTWeekDate.WEEK_OF_YEAR));
      Assertions.assertEquals(DayOfWeek.of(dayOfWeek), ((ASTWeekDate) realAst).toDayOfWeek());
    }
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isLocalDate(),
                !((ASTWeekDate) realAst).isPresentDayOfWeek()
            )
            .filter(i -> i).count());
    if (realAst.isLocalDate()) {
      Assertions.assertEquals(isoWeekDate(year, week, dayOfWeek), realAst.toLocalDate());
    }
  }

  // Expected dates are taken from ISO 8601-1 (1985-W15-5, Amd. 1 for 2019-W01-1)
  // or cover years whose 1 January is a Friday, Saturday or Sunday
  @DisplayName("Week Date Known Values")
  @ParameterizedTest(name = "Input: {0}")
  @CsvSource({
      "1985-W15-5, 1985-04-12", // ISO 8601-1
      "2017-W48-1, 2017-11-27", // 2017-01-01 is a Sunday
      "2019-W01-1, 2018-12-31", // ISO 8601-1 Amd. 1
      "2020-W53-5, 2021-01-01", // 2021-01-01 is a Friday
      "2021-W01-1, 2021-01-04", // 2021-01-01 is a Friday
      "2022W011, 2022-01-03"    // 2022-01-01 is a Saturday
  })
  public void testWeekDateKnownValues(String input, String expectedDate) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());

    ASTDate realAst = (ASTDate) ast.getInstant();
    LocalDate expected = LocalDate.parse(expectedDate);

    Assertions.assertEquals(expected, realAst.toLocalDate());
    Assertions.assertEquals(expected, realAst.toTemporal());
    // YEAR is the calendar year of the date, not the week-based year
    Assertions.assertEquals(expected.getYear(), realAst.get(YEAR));
    Assertions.assertEquals(expected.get(IsoFields.WEEK_BASED_YEAR), realAst.get(IsoFields.WEEK_BASED_YEAR));
    Assertions.assertEquals(expected.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), realAst.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    // WEEK_OF_YEAR counts weeks within the calendar year, not the ISO week
    Assertions.assertTrue(realAst.isSupported(ASTWeekDate.WEEK_OF_YEAR));
    Assertions.assertEquals(expected.get(WeekFields.ISO.weekOfYear()), realAst.get(ASTWeekDate.WEEK_OF_YEAR));
    Assertions.assertEquals(expected.range(WeekFields.ISO.weekOfYear()), realAst.range(ASTWeekDate.WEEK_OF_YEAR));

    ASTWeekDate weekDate = (ASTWeekDate) realAst;
    Assertions.assertEquals(expected.getYear(), weekDate.getCalendarYear());
    Assertions.assertEquals(expected.getDayOfWeek(), weekDate.toDayOfWeek());
  }

  // 2021 has 52 ISO weeks, so W53 is out of range like W00 and W54
  @DisplayName("Week Date Invalid Week")
  @ParameterizedTest(name = "Input: {0}")
  @CsvSource({"2021-W53-1", "2021-W00-1", "2021-W54-1"})
  public void testWeekDateInvalidWeek(String input) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());

    ASTDate realAst = (ASTDate) ast.getInstant();

    Assertions.assertThrows(DateTimeException.class, realAst::toLocalDate);
    Assertions.assertThrows(DateTimeException.class, () -> realAst.get(YEAR));
    Assertions.assertThrows(DateTimeException.class, () -> realAst.get(ASTWeekDate.WEEK_OF_YEAR));
    Assertions.assertThrows(DateTimeException.class, ((ASTWeekDate) realAst)::getCalendarYear);
  }

  // 2020 has 53 ISO weeks, 2021 has 52
  @DisplayName("Week Date Range")
  @ParameterizedTest(name = "Input: {0}")
  @CsvSource({"2020-W01-1, 53", "2021-W01-1, 52", "2021-W01, 52"})
  public void testWeekDateRange(String input, int weeks) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());
    ASTDate realAst = (ASTDate) ast.getInstant();

    Assertions.assertEquals(ValueRange.of(1, weeks), realAst.range(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    Assertions.assertEquals(IsoFields.WEEK_BASED_YEAR.range(), realAst.range(IsoFields.WEEK_BASED_YEAR));
    if (!((ASTWeekDate) realAst).isPresentDayOfWeek()) {
      Assertions.assertThrows(UnsupportedTemporalTypeException.class, () -> realAst.range(YEAR));
      Assertions.assertThrows(UnsupportedTemporalTypeException.class, () -> realAst.range(ASTWeekDate.WEEK_OF_YEAR));
    }
  }

  /**
   * @return the date of an ISO week date, computed with java.time's ISO week-based fields
   */
  protected static LocalDate isoWeekDate(int weekBasedYear, int week, int dayOfWeek) {
    return LocalDate.EPOCH
        .with(IsoFields.WEEK_BASED_YEAR, weekBasedYear)
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
        .with(DAY_OF_WEEK, dayOfWeek);
  }
  
  @DisplayName("ISO Time Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#isoTimes")
  public void testIsoTimeConversion(String input, int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTTime.class, ast.getInstant());
    ASTTime realAst = (ASTTime) ast.getInstant();
    
    Assertions.assertTrue(realAst.isSupported(HOUR_OF_DAY));
    Assertions.assertEquals(hour, realAst.get(HOUR_OF_DAY));
    
    if (minute == -1) {
      Assertions.assertFalse(realAst.isSupported(MINUTE_OF_HOUR));
    } else {
      Assertions.assertTrue(realAst.isSupported(MINUTE_OF_HOUR));
      Assertions.assertEquals(minute, realAst.get(MINUTE_OF_HOUR));
    }
    
    if (second == -1) {
      Assertions.assertFalse(realAst.isSupported(SECOND_OF_MINUTE));
    } else {
      Assertions.assertTrue(realAst.isSupported(SECOND_OF_MINUTE));
      Assertions.assertEquals(second, realAst.get(SECOND_OF_MINUTE));
    }
    
    int nanos = 0;
    if (decimalDigits.isEmpty() || second == -1) {
      Assertions.assertFalse(realAst.isSupported(NANO_OF_SECOND));
    } else {
      Assertions.assertTrue(realAst.isSupported(NANO_OF_SECOND));
      nanos = new BigDecimal("0." + decimalDigits + "e9").intValueExact();
      Assertions.assertEquals(nanos, realAst.get(NANO_OF_SECOND));
    }
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isExactlyLocalTime(),
                realAst.isOffsetTime(),
                !((ASTISOTime) realAst).isPresentSecond()
            )
            .filter(i -> i).count());
    if (realAst.isExactlyLocalTime()) {
      Assertions.assertEquals(
          LocalTime.of(hour, minute, second, nanos),
          realAst.toLocalTime()
      );
    } else if (realAst.isOffsetTime()) {
      Assertions.assertEquals(
          OffsetTime.of(hour, minute, second, nanos, ZoneOffset.ofHours(timeShift.orElseThrow())),
          realAst.toOffsetTime()
      );
    }
  }
  
  @DisplayName("ISO Time Shift Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#isoTimeShifts")
  public void testIsoTimeShiftConversion(String input, String expectedOffset) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    ASTInstant realAst = ast.getInstant();

    Assertions.assertTrue(realAst.isSupported(OFFSET_SECONDS));
    Assertions.assertEquals(ZoneOffset.of(expectedOffset).getTotalSeconds(), realAst.get(OFFSET_SECONDS));
  }

  @DisplayName("Calendar DateTime Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDateTimes")
  public void testCalendarDateTimeConversion(String input, int year, int month, int day,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDateTime.class, ast.getInstant());
    ASTDateTime realAst = (ASTDateTime) ast.getInstant();
    
    int nanos = 0;
    if (!decimalDigits.isEmpty() && second != -1) {
      nanos = new BigDecimal("0." + decimalDigits + "e9").intValueExact();
    }
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isExactlyLocalDateTime(),
                realAst.isOffsetDateTime(),
                !((ASTISOTime) realAst.getTime()).isPresentSecond()
            )
            .filter(i -> i).count());
    if (realAst.isExactlyLocalDateTime()) {
      Assertions.assertEquals(
          LocalDateTime.of(year, month, day, hour, minute, second, nanos),
          realAst.toLocalDateTime()
      );
    } else if (realAst.isOffsetDateTime()) {
      Assertions.assertEquals(
          OffsetDateTime.of(year, month, day, hour, minute, second, nanos, ZoneOffset.ofHours(timeShift.orElseThrow())),
          realAst.toOffsetDateTime()
      );
    }
  }
  
  @DisplayName("Ordinal DateTime Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#ordinalDateTimes")
  public void testOrdinalDateTimeConversion(String input, int year, int dayOfYear,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDateTime.class, ast.getInstant());
    ASTDateTime realAst = (ASTDateTime) ast.getInstant();
    
    int nanos = 0;
    if (!decimalDigits.isEmpty() && second != -1) {
      nanos = new BigDecimal("0." + decimalDigits + "e9").intValueExact();
    }
    if (second == -1) {
      second = 0; // Default value of -1 would cause an error
    }
    if (minute == -1) {
      minute = 0; // Same as above
    }
    int offset = timeShift.orElse(0);
    LocalDate expectedDate = LocalDate.ofYearDay(year, dayOfYear);
    LocalTime expectedTime = LocalTime.of(hour, minute, second, nanos);
    OffsetTime expectedOffsetTime = expectedTime.atOffset(ZoneOffset.ofHours(offset));
    LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
    OffsetDateTime expectedOffsetDateTime = expectedDate.atTime(expectedOffsetTime);
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isExactlyLocalDateTime(),
                realAst.isOffsetDateTime(),
                !((ASTISOTime) realAst.getTime()).isPresentSecond()
            )
            .filter(i -> i).count());
    if (realAst.isExactlyLocalDateTime()) {
      Assertions.assertEquals(expectedDateTime, realAst.toLocalDateTime());
    } else if (realAst.isOffsetDateTime()) {
      Assertions.assertEquals(expectedOffsetDateTime, realAst.toOffsetDateTime());
    }
  }
  
  @DisplayName("Week DateTime Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#weekDateTimes")
  public void testWeekDateTimeConversion(String input, int year, int week, int dayOfWeek,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) throws IOException {
    ASTTemporal ast = CombinedTemporalsTestMill.parser().parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDateTime.class, ast.getInstant());
    ASTDateTime realAst = (ASTDateTime) ast.getInstant();
    
    int nanos = 0;
    if (!decimalDigits.isEmpty() && second != -1) {
      nanos = new BigDecimal("0." + decimalDigits + "e9").intValueExact();
    }
    if (second == -1) {
      second = 0; // Default value of -1 would cause an error
    }
    if (minute == -1) {
      minute = 0; // Same as above
    }
    int offset = timeShift.orElse(0);
    LocalDate expectedDate = isoWeekDate(year, week, dayOfWeek);
    LocalTime expectedTime = LocalTime.of(hour, minute, second, nanos);
    OffsetTime expectedOffsetTime = expectedTime.atOffset(ZoneOffset.ofHours(offset));
    LocalDateTime expectedDateTime = expectedDate.atTime(expectedTime);
    OffsetDateTime expectedOffsetDateTime = expectedDate.atTime(expectedOffsetTime);
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isExactlyLocalDateTime(),
                realAst.isOffsetDateTime(),
                !((ASTISOTime) realAst.getTime()).isPresentSecond()
            )
            .filter(i -> i).count());
    if (realAst.isExactlyLocalDateTime()) {
      Assertions.assertEquals(expectedDateTime, realAst.toLocalDateTime());
    } else if (realAst.isOffsetDateTime()) {
      Assertions.assertEquals(expectedOffsetDateTime, realAst.toOffsetDateTime());
    }
    
    // The date-time uses the ranges refined by the week date
    Assertions.assertEquals(expectedDate.range(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
        realAst.range(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    Assertions.assertEquals(week, realAst.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
    Assertions.assertEquals(expectedDate.get(WeekFields.ISO.weekOfYear()), realAst.get(ASTWeekDate.WEEK_OF_YEAR));
    Assertions.assertEquals(expectedDate.getYear(), realAst.get(YEAR));
    Assertions.assertEquals(hour, realAst.get(HOUR_OF_DAY));
  }
  
  @DisplayName("Week Date Reparsing")
  @ParameterizedTest(name = "Input: {0} -> {1}")
  @CsvSource({"2017W481, 2015W14, -1", "2015W14, 2017W481, 1"})
  public void testWeekDateReparsing(String input, String newSource, int dayOfWeek) {
    ASTBasicWeekDate ast = CombinedTemporalsTestMill.basicWeekDateBuilder().setSource(input).build();
    ast.setSource(newSource);
    ISOTemporals2ndParser.doParse(ast);
    
    Assertions.assertEquals(dayOfWeek != -1, ast.isPresentDayOfWeek());
    Assertions.assertEquals(dayOfWeek, ast.isPresentDayOfWeek() ? ast.getDayOfWeek() : -1);
    Assertions.assertEquals(dayOfWeek != -1, ast.isLocalDate());
  }
  
  @DisplayName("German Date Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanDates")
  public void testGermanDateConversion(String input, int year, int month, int day) throws IOException {
    ASTDate ast = CombinedTemporalsTestMill.parser().parse_StringDEDate(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    
    Year expectedYear = Year.of(year);
    YearMonth expectedYearMonth = expectedYear.atMonth(month == -1 ? 1 : month);
    LocalDate expectedDate = expectedYearMonth.atDay(day == -1 ? 1 : day);
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                ast.isExactlyYear(),
                ast.isExactlyYearMonth(),
                ast.isLocalDate()
            )
            .filter(i -> i).count());
    if (ast.isExactlyYear()) {
      Assertions.assertEquals(expectedYear, ast.toYear());
    } else if (ast.isExactlyYearMonth()) {
      Assertions.assertEquals(expectedYearMonth, ast.toYearMonth());
    } else {
      Assertions.assertEquals(expectedDate, ast.toLocalDate());
    }
  }
  
  @DisplayName("German Time Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanTimes")
  public void testGermanTimeConversion(String input, int hour, int minute, int second) throws IOException {
    ASTTime ast = CombinedTemporalsTestMill.parser().parse_StringTime(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    
    LocalTime expectedTime = LocalTime.of(hour, minute == -1 ? 0 : minute, second == -1 ? 0 : second);
    Assertions.assertFalse(ast.isOffsetTime());
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                ast.isExactlyLocalTime(),
                !((ASTDETime) ast).isPresentSecond()
            )
            .filter(i -> i).count());
    if (ast.isExactlyLocalTime()) {
      Assertions.assertEquals(expectedTime, ast.toLocalTime());
    }
  }
  
  @DisplayName("German DateTime Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanDateTimes")
  public void testGermanDateTimeConversion(String input, int year, int month, int day, int hour, int minute, int second) throws IOException {
    ASTDateTime ast = CombinedTemporalsTestMill.parser().parse_StringDateTime(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    
    if (minute == -1) {
      minute = 0;
    }
    if (second == -1) {
      second = 0;
    }
    LocalDateTime expectedDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
    Assertions.assertFalse(ast.isOffsetDateTime());
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                ast.isExactlyLocalDateTime(),
                !((ASTDETime) ast.getTime()).isPresentSecond()
            )
            .filter(i -> i).count());
    if (ast.isExactlyLocalDateTime()) {
      Assertions.assertEquals(expectedDateTime, ast.toLocalDateTime());
    }
  }
}
