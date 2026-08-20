/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.parsing;

import de.monticore.temporal.parsing.isotemporals4parsingtest.ISOTemporals4ParsingTestMill;
import de.monticore.temporal.parsing.isotemporals4parsingtest._parser.ISOTemporals4ParsingTestParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Test cases taken from ISO 8601-1.
 */
public class ISOTemporals4ParsingTest {

  private ISOTemporals4ParsingTestParser isoParser;

  @BeforeEach
  void setup() {
    LogStub.init();
    LogStub.clearFindings();
    LogStub.clearPrints();
    Log.enableFailQuick(false);
    ISOTemporals4ParsingTestMill.init();
    isoParser = ISOTemporals4ParsingTestMill.parser();
  }
  
  @AfterEach
  void tearDown() {
    ISOTemporals4ParsingTestMill.reset();
  }

  protected static Stream<String> calendarDates() {
    return Stream.of(
        // complete
        "19850412", "1985-04-12",
        // reduced precision
        "1985-04", "1985", "198", "19",
        // expanded
        "+0019850412", "+001985-04-12", "+001985-04", "+001985",
        "-0019850412", "-001985-04-12", "-001985-04", "-001985",
        "+00198", "+0019"
    );
  }

  @DisplayName("Calendar Date and Time")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("calendarDates")
  public void testCalendarDate(String input) {
    checkValid(input);
  }

  protected static Stream<String> calendarDatesAndTimes() {
    return Stream.of(
        // + time of day, complete
        "19850412T232030", "19850412T232030Z", "19850412T232030+0400",
        "19850412T232030+04", "1985-04-12T23:20:30", "1985-04-12T23:20:30Z",
        "1985-04-12T23:20:30+04:00", "1985-04-12T23:20:30+04",
        // ... + fraction component
        "19850412T232030.234", "19850412T232030,12Z", "19850412T232030,123+0400",
        "19850412T232030.7644+04", "1985-04-12T23:20:30,23", "1985-04-12T23:20:30.43Z",
        "1985-04-12T23:20:30.1233+04:00", "1985-04-12T23:20:30.4321+04",
        // + time of day, reduced precision
        "19850412T1015", "1985-04-12T10:15",
        // ... + fraction component
        "19850412T1015.89", "1985-04-12T10:15,5750"
    );
  }

  @DisplayName("Calendar Date")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("calendarDatesAndTimes")
  public void testCalendarDateAndTime(String input) {
    checkValid(input);
  }

  protected static Stream<String> calendarDatesWS() {
    return Stream.of(
        // complete
        "1985 0412", "198504 12", "1985 -04-12", "1985- 04-12", "1985-04 -12",
        "1985-04- 12",
        // reduced precision
        "1985 -04", "1985- 04",
        // expanded
        "- 0019850412", "+001985 0412", "+00198504 12", "+001985 -04-12",
        "+001985- 04-12",
        "+001985-04 -12", "+001985-04- 12", "+ 001985-04-12", "+ 001985-04",
        "+001985 -04",
        "+001985- 04", "+ 001985",
        "+ 00198", "+ 0019",
        // + time of day, complete
        "19850412 T232030", "19850412 T232030Z", "19850412 T232030+0400",
        "19850412T 232030", "19850412T 232030Z", "19850412T 232030+0400",
        "19850412T23 2030", "19850412T23 2030Z", "19850412T23 2030+0400",
        "19850412T2320 30", "19850412T2320 30Z", "19850412T2320 30+0400",
        "19850412T232030 Z", "19850412T232030 +0400", "19850412T232030+ 0400",
        "19850412T232030 +04 00",

        "19850412 T232030+04", "19850412T 232030+04", "19850412T23 2030+04",
        "19850412T2320 30+04", "19850412T232030 +04", "19850412T232030+ 04",

        "1985-04-12 T23:20:30", "1985-04-12T 23:20:30", "1985-04-12T23 :20:30",
        "1985-04-12T23: 20:30", "1985-04-12T23:20 :30", "1985-04-12T23:20: 30",

        "1985-04-12 T23:20:30Z", "1985-04-12T 23:20:30Z",
        "1985-04-12T23 :20:30Z", "1985-04-12T23: 20:30Z",
        "1985-04-12T23:20 :30Z", "1985-04-12T23:20: 30Z",
        "1985-04-12T23:20:30 Z",

        "1985-04-12 T23:20:30+04:00", "1985-04-12T 23:20:30+04:00",
        "1985-04-12T23 :20:30+04:00", "1985-04-12T23: 20:30+04:00",
        "1985-04-12T23:20 :30+04:00", "1985-04-12T23:20: 30+04:00",
        "1985-04-12T23:20:30 +04:00", "1985-04-12T23:20:30+ 04:00",
        "1985-04-12T23:20:30+04 :00", "1985-04-12T23:20:30+04: 00",

        "1985-04-12 T23:20:30+04", "1985-04-12T 23:20:30+04",
        "1985-04-12T23 :20:30+04", "1985-04-12T23: 20:30+04",
        "1985-04-12T23:20 :30+04", "1985-04-12T23:20: 30+04",
        "1985-04-12T23:20:30 +04", "1985-04-12T23:20:30+ 04",

        // + time of day, reduced precision
        "19850412 T1015", "19850412T 1015", "19850412T10 15",

        "1985-04-12 T10:15", "1985-04-12T 10:15", "1985-04-12T10 :15",
        "1985-04-12T10: 15");
  }

  @DisplayName("Calendar Date – no white space")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("calendarDatesWS")
  public void testCalendarDateWS(String input) {
    checkInvalidWS(input);
  }

  protected static Stream<String> ordinalDates() {
    return Stream.of(
        // complete
        "1985102", "1985-102",
        // expanded
        "+001985102", "+001985-102",
        // + time of day, complete
        "1985102T232030", "1985102T232030Z", "1985102T232030+0400",
        "1985102T232030+04", "1985-102T23:20:30", "1985-102T23:20:30Z",
        "1985-102T23:20:30+04:00", "1985-102T23:20:30+04",
        // ... + fraction component
        "1985102T232030,123", "1985102T232030.324Z", "1985102T232030.34+0400",
        "1985102T232030.345+04", "1985-102T23:20:30,1235",
        "1985-102T23:20:30.234Z", "1985-102T23:20:30.34+04:00",
        "1985-102T23:20:30.34+04",
        // + time of day, reduced precision
        "1985102T1015Z", "1985-102T10:15Z",
        // ... + fraction component
        "1985102T1015,1Z", "1985-102T10:15.1234Z");
  }

  @DisplayName("Ordinal Date")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("ordinalDates")
  public void testOrdinalDate(String input) {
    checkValid(input);
  }

  protected static Stream<String> ordinalDatesWS() {
    return Stream.of(
        // complete
        "1985 102", "1985 -102", "1985- 102",
        // expanded
        "+ 001985102", "+ 001985-102", "+001985 -102", "+001985- 102",
        // + time of day, complete
        "1985102 T232030", "1985 -102T23:20:30", "1985- 102T23:20:30Z",
        "1985-102 T23:20:30Z", "1985-102T 23:20:30+04:00",
        "1985-102T 23:20:30+04",
        // + time of day, reduced precision
        "1985102 T1015Z", "1985-102 T10:15Z");
  }

  @DisplayName("Ordinal Date – no white space")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("ordinalDatesWS")
  public void testOrdinalDateWS(String input) {
    checkInvalidWS(input);
  }

  protected static Stream<String> weekDates() {
    return Stream.of(
        // complete
        "1985W155", "1985-W15-5",
        // reduced precision
        "1985W15", "1985-W15",
        // expanded
        "+001985W155", "+001985-W15-5", "+001985W15", "+001985-W15",
        // + time of day, complete
        "1985W155T232030", "1985W155T232030Z", "1985W155T232030+0400",
        "1985W155T232030+04", "1985-W15-5T23:20:30", "1985-W15-5T23:20:30Z",
        "1985-W15-5T23:20:30+04:00", "1985-W15-5T23:20:30+04",
        "1985W155T232030,1", "1985W155T232030.2Z", "1985W155T232030,12+0400",
        // ... + fraction
        "1985W155T232030.789+04", "1985-W15-5T23:20:30,123907",
        "1985-W15-5T23:20:30,1234Z", "1985-W15-5T23:20:30.234+04:00",
        "1985-W15-5T23:20:30.2345+04",
        // + time of day, reduced precision
        "1985W155T1015+0400", "1985-W15-5T10:15+04",
        // ... + fraction component
        "1985W155T1015.5+0400", "1985-W15-5T10:15,1234+04");
  }

  @DisplayName("Week Date")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("weekDates")
  public void testWeekDate(String input) {
    checkValid(input);
  }

  protected static Stream<String> weekDatesWS() {
    return Stream.of(
        // complete
        "1985 W155", "1985W 155", "1985W15 5", "1985 W15 5", "1985 -W15-5",
        "1985- W15-5", "1985-W 15-5", "1985-W15 -5", "1985-W15- 5",
        // reduced precision
        "1985 W15", "1985W 15", "1985 -W15", "1985- W15", "1985-W 15",
        // expanded
        "+ 001985W155", "+ 001985-W15-5", "+ 001985W15", "+ 001985-W15",
        "+001985 W155", "+001985 -W15-5", "+001985 W15", "+001985 -W15",
        "+001985W 155", "+001985- W15-5", "+001985W 15", "+001985- W15",
        "+001985W15 5", "+001985-W 15-5", "+001985-W 15", "+001985-W15 -5",
        "+001985-W15- 5",
        // + time of day, complete
        "1985 W155T232030", "1985W 155T232030Z", "1985W15 5T232030+0400",
        "1985W155 T232030+04", "1985-W15-5T 23:20:30",
        // + time of day, reduced precision
        "1985 W155T1015+0400", "1985-W15-5T 10:15+04");
  }

  @DisplayName("Week Date – no white space")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("weekDatesWS")
  public void testWeekDateWS(String input) {
    checkInvalidWS(input);
  }

  protected static Stream<String> timeOnly() {
    return Stream.of(
        // local time of day, complete
        "T232050", "T23:20:50", "23:20:50",
        // local time of day, reduced precision
        "T2350", "T23:20", "23:20", "T23",
        // local time of day, decimal fraction
        "T232030.5", "T23:20:30.5", "23:20:30.5", "T2320.8", "T23:20.8",
        "23:20,8", "T23.3", "T232030,5", "T23:20:30,5", "23:20:30,5", "T2320,8",
        "T23:20,8", "23:20,8", "T23,3",
        // local time of day, longer decimal fraction
        "T232030.51", "T23:20:30.512", "23:20:30.5123", "T2320.89876543",
        "T23:20.8987654", "23:20,898765", "T23.39876", "T232030,5456",
        "T23:20:30,545", "23:20:30,54", "T2320,89876", "T23:20,898765",
        "23:20,8987654", "T23,39876543",
        // beginning of the day
        "T000000", "T00:00:00", "00:00:00",
        // UTC of day
        "T232030Z", "T2320Z", "T23Z", "T23:20:30Z", "T23:20Z", "23:20:30Z",
        "23:20Z",
        // UTC of day, fraction
        "T232030.51Z", "T23:20:30.5Z", "23:20:30.5123Z", "T2320.8Z",
        "T23:20.8987654Z", "23:20,8Z", "T23.39876Z", "T232030,5Z",
        "T23:20:30,545Z", "23:20:30,5Z", "T2320,89876Z", "T23:20,8Z",
        "23:20,8987654Z", "T23,3Z",
        // time shift
        "T152746+0100", "T152746-0500", "T152746+01", "T152746-05",
        "T15:27:46+01:00", "T15:27:46-05:00", "T15:27:46-05:00",
        "T15:27:46-05:00", "15:27:46+01:00", "15:27:46-05:00", "15:27:46-05:00",
        "15:27:46-05:00",
        // UTC of day, fraction
        "T232030.51+0100", "23:20:30.5123+01", "T2320.8-05",
        "T23:20.8987654+01:00", "23:20,8-05:00", "T23.39876-05:00",
        "T232030,5+1200",
        "T2320,89876-1700", "T23:20,8+13:30", "23:20,8987654-12:59",
        "T23,3+06:00");
  }

  @DisplayName("Time Only")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("timeOnly")
  public void testTimeOnly(String input) {
    checkValid(input);
  }

  void checkValid(String input) {
    Assertions.assertDoesNotThrow(() ->
        isoParser.parse_String(input).orElseThrow());
  }

  void checkInvalidWS(String input) {
    var optTemporalExpression =
        Assertions.assertDoesNotThrow(() -> isoParser.parse_String(input));
    Assertions.assertFalse(optTemporalExpression.isPresent());
    Assertions.assertTrue(Log.getFindingsCount() >= 1, () -> {
      StringBuilder sb = new StringBuilder();
      for (Finding finding : Log.getFindings()) {
        sb.append(finding);
      }
      return sb.toString();
    });
  }
}
