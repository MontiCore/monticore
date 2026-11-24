/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.combinedtemporalstest.CombinedTemporalsTestMill;
import de.monticore.temporal.combinedtemporalstest._ast.ASTTemporal;
import de.monticore.temporal.combinedtemporalstest._parser.CombinedTemporalsTestParser;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
 * Times and date-times with time shifts, in particular negative ones with minutes,
 * which the lexer must not take as part of a date-time token.
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
   * Times without date and with a negative extended time shift, which
   * {@link TemporalTestCases#isoTimeShifts()} does not contain.
   */
  protected static Stream<Arguments> negativeExtendedTimeShifts() {
    return Stream.of(
        Arguments.of("12:30:01-05:30", "-05:30"),
        Arguments.of("T12:30:01-05:30", "-05:30"),
        Arguments.of("12:30:01-05:00", "-05:00"),
        Arguments.of("T12:30:01-05:00", "-05:00"),
        Arguments.of("12:30:01-00:30", "-00:30"),
        Arguments.of("T12:30:01-00:30", "-00:30")
    );
  }

  // All inputs denote 2017-12-04 or no date, at 12:30:01.
  // A 'T' after the first character separates a date from the time.
  @DisplayName("ISO Time and Date-Time with Time Shift")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource({"de.monticore.temporal.TemporalTestCases#isoTimeShifts",
      "negativeExtendedTimeShifts"})
  void testTimeShift(String input, String expectedOffset) throws IOException {
    ASTTemporal ast = parser.parse_String(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
    ZoneOffset offset = ZoneOffset.of(expectedOffset);

    if (input.indexOf('T', 1) > 0) {
      ASTISODateTime dateTime =
          Assertions.assertInstanceOf(ASTISODateTime.class, ast.getInstant());
      Assertions.assertEquals(OffsetDateTime.of(DATE, TIME, offset), dateTime.toOffsetDateTime());
    } else {
      ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class, ast.getInstant());
      Assertions.assertEquals(OffsetTime.of(TIME, offset), time.toOffsetTime());
    }
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
