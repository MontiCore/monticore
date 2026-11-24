/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;

/**
 * Times and date-times with every kind of time shift, in particular negative
 * ones with minutes, which the lexer must not split into a date-time token.
 */
@TestWithMCLanguage(CombinedTemporalsTestMill.class)
public class ISOTimeShiftLexingTest {

  protected static final LocalDate DATE = LocalDate.of(2017, 12, 4);

  protected static final LocalTime TIME = LocalTime.of(12, 30, 1);

  CombinedTemporalsTestParser parser;

  @BeforeEach
  void setup() {
    parser = CombinedTemporalsTestMill.parser();
  }

  /**
   * Time shifts as {basic notation, extended notation, expected offset}.
   */
  protected static Stream<Arguments> timeShifts() {
    return Stream.of(
        Arguments.of("Z", "Z", ZoneOffset.UTC),
        Arguments.of("+05", "+05", ZoneOffset.ofHours(5)),
        Arguments.of("-05", "-05", ZoneOffset.ofHours(-5)),
        Arguments.of("+0530", "+05:30", ZoneOffset.ofHoursMinutes(5, 30)),
        Arguments.of("-0530", "-05:30", ZoneOffset.ofHoursMinutes(-5, -30)),
        Arguments.of("-0500", "-05:00", ZoneOffset.ofHoursMinutes(-5, 0)),
        Arguments.of("-0030", "-00:30", ZoneOffset.ofHoursMinutes(0, -30))
    );
  }

  protected static Stream<Arguments> times() {
    return timeShifts().flatMap(a -> {
      Object[] shift = a.get();
      return Stream.of(
          Arguments.of("T123001" + shift[0], shift[2]),
          Arguments.of("12:30:01" + shift[1], shift[2]),
          Arguments.of("T12:30:01" + shift[1], shift[2]));
    });
  }

  protected static Stream<Arguments> dateTimes() {
    return timeShifts().flatMap(a -> {
      Object[] shift = a.get();
      return Stream.of(
          Arguments.of("20171204T123001" + shift[0], shift[2]),
          Arguments.of("2017-12-04T12:30:01" + shift[1], shift[2]));
    });
  }

  @DisplayName("ISO Time with Time Shift")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("times")
  void testTime(String input, ZoneOffset offset) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());

    Assertions.assertEquals(OffsetTime.of(TIME, offset), time.toOffsetTime());
  }

  @DisplayName("ISO Date-Time with Time Shift")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("dateTimes")
  void testDateTime(String input, ZoneOffset offset) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ASTISODateTime dateTime = Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());

    Assertions.assertEquals(OffsetDateTime.of(DATE, TIME, offset), dateTime.toOffsetDateTime());
  }

  // Times without seconds are no OffsetTime, so hour, minute and offset are compared directly.
  // 18:30-08:00 is the example in Temporal.md.
  @DisplayName("ISO Time without Seconds with Time Shift")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource
  void testTimeWithoutSeconds(String input, LocalTime expectedTime, ZoneOffset offset)
      throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());

    Assertions.assertEquals(expectedTime.getHour(), time.get(HOUR_OF_DAY));
    Assertions.assertEquals(expectedTime.getMinute(), time.get(MINUTE_OF_HOUR));
    Assertions.assertFalse(time.isSupported(SECOND_OF_MINUTE));
    Assertions.assertEquals(offset.getTotalSeconds(), time.get(OFFSET_SECONDS));
  }

  protected static Stream<Arguments> testTimeWithoutSeconds() {
    return Stream.of(
        Arguments.of("18:30-08:00", LocalTime.of(18, 30), ZoneOffset.ofHours(-8)),
        Arguments.of("12:00-05:30", LocalTime.of(12, 0), ZoneOffset.ofHoursMinutes(-5, -30)),
        Arguments.of("T1200-0530", LocalTime.of(12, 0), ZoneOffset.ofHoursMinutes(-5, -30))
    );
  }

  // A date-time needs the designator T between date and time (ISO 8601-1, 5.4.2).
  // Without it, the input is no date-time token: the parser reads a calendar date
  // and rejects the ':' after it.
  @DisplayName("ISO Date-Time without T")
  @ParameterizedTest(name = "Input: {0}")
  @CsvSource({"2017-12-0412:30, 12", "2017-12-0412:30:01-05:00, 12", "20171204123001-05:00, 17"})
  void testDateTimeWithoutT(String input, int colonColumn) throws IOException {
    Optional<ASTTemporal> ast = parser.parse_String(input);

    Assertions.assertTrue(ast.isEmpty());
    Assertions.assertEquals(1, Log.getFindings().size());
    Finding finding = MCAssertions.assertHasFinding(Finding::isError);
    Assertions.assertTrue(finding.getMsg().startsWith("mismatched input ':'"), finding.getMsg());
    Assertions.assertEquals(colonColumn,
        finding.getSourcePosition().map(SourcePosition::getColumn).orElse(-1));
  }
}
