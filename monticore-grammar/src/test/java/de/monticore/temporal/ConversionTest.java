package de.monticore.temporal;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.isotemporals._ast.ASTCalendarDate;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.monticore.temporal.isotemporals._ast.ASTWeekDate;
import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._ast.ASTDateTime;
import de.monticore.temporal.temporalbasis._ast.ASTTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.*;
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
      int year, int month, int day) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
  void testOrdinalDateConversion(String input, int year, int dayOfYear) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
  public void testWeekDateConversion(String input, int year, int week, int dayOfWeek) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
    Assertions.assertTrue(ast.isPresentInstant());
    Assertions.assertInstanceOf(ASTDate.class, ast.getInstant());
    ASTDate realAst = (ASTDate) ast.getInstant();
    
    Assertions.assertTrue(realAst.isSupported(YEAR));
    Assertions.assertEquals(year, realAst.get(YEAR));
    
    Assertions.assertTrue(realAst.isSupported(WeekFields.ISO.weekOfYear()));
    Assertions.assertEquals(week, realAst.get(WeekFields.ISO.weekOfYear()));
    
    if (dayOfWeek == -1) {
      Assertions.assertFalse(realAst.isSupported(DAY_OF_WEEK));
    } else {
      Assertions.assertTrue(realAst.isSupported(DAY_OF_WEEK));
      Assertions.assertEquals(dayOfWeek, realAst.get(DAY_OF_WEEK));
    }
    
    // Exactly one of these boolean conditions has to be true
    Assertions.assertEquals(1,
        Stream.of(
                realAst.isLocalDate(),
                !((ASTWeekDate) realAst).isPresentDayOfWeek()
            )
            .filter(i -> i).count());
    if (realAst.isLocalDate()) {
      Assertions.assertEquals(
          LocalDate.ofYearDay(year, 1)
              .plusWeeks(week - 1)
              .with(DAY_OF_WEEK, dayOfWeek),
          realAst.toLocalDate());
    }
  }
  
  @DisplayName("ISO Time Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#isoTimes")
  public void testIsoTimeConversion(String input, int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
  
  @DisplayName("Calendar DateTime Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#calendarDateTimes")
  public void testCalendarDateTimeConversion(String input, int year, int month, int day,
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
      int hour, int minute, int second, String decimalDigits, Optional<Integer> timeShift) {
    ASTTemporal ast = Assertions.assertDoesNotThrow(() ->
        CombinedTemporalsTestMill.parser().parse_String(input).orElseThrow());
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
    LocalDate expectedDate = LocalDate
        .ofYearDay(year, 1)
        .plusWeeks(week - 1)
        .with(DAY_OF_WEEK, dayOfWeek);
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
  
  @DisplayName("German Date Conversion")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("de.monticore.temporal.TemporalTestCases#germanDates")
  public void testGermanDateConversion(String input, int year, int month, int day) {
    ASTDate ast = Assertions.assertDoesNotThrow(
        () -> CombinedTemporalsTestMill.parser().parse_StringDEDate(input).orElseThrow());
    
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
  public void testGermanTimeConversion(String input, int hour, int minute, int second) {
    ASTTime ast = Assertions.assertDoesNotThrow(
        () -> CombinedTemporalsTestMill.parser().parse_StringTime(input).orElseThrow());
    
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
  public void testGermanDateTimeConversion(String input, int year, int month, int day, int hour, int minute, int second) {
    ASTDateTime ast = Assertions.assertDoesNotThrow(
        () -> CombinedTemporalsTestMill.parser().parse_StringDateTime(input).orElseThrow());
    
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
