package de.monticore.temporal;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")       // Linter can't detect that this class is used by JUnit
public class TemporalTestCases {
  
  protected static Stream<Arguments> calendarDates() {
    return Stream.of(
        Arguments.of("20171204", -1, -1, 2017, 12, 4),
        Arguments.of("2017-12-04", -1, -1, 2017, 12, 4),
        Arguments.of("2017-12", -1, -1, 2017, 12, -1),
        Arguments.of("2017", -1, -1, 2017, -1, -1),
        Arguments.of("201", -1, 201, -1, -1, -1),
        Arguments.of("20", 20, -1, -1, -1, -1),
        
        Arguments.of("+102017-12-04", -1, -1, 102017, 12, 4),
        Arguments.of("-102017-12-04", -1, -1, -102017, 12, 4),
        Arguments.of("+1020171204", -1, -1, 102017, 12, 4),
        Arguments.of("-1020171204", -1, -1, -102017, 12, 4),
        Arguments.of("+102017-12", -1, -1, 102017, 12, -1),
        Arguments.of("-102017-12", -1, -1, -102017, 12, -1),
        Arguments.of("+102017", -1, -1, 102017, -1, -1),
        Arguments.of("-102017", -1, -1, -102017, -1, -1),
        Arguments.of("+10201", -1, 10201, -1, -1, -1),
        Arguments.of("-10201", -1, -10201, -1, -1, -1),
        Arguments.of("+1020", 1020, -1, -1, -1, -1),
        Arguments.of("-1020", -1020, -1, -1, -1, -1)
    );
  }
  
  protected static Stream<Arguments> ordinalDates() {
    return Stream.of(
        Arguments.of("2017-338", 2017, 338),
        Arguments.of("2017338", 2017, 338),
        
        Arguments.of("+002017-338", 2017, 338),
        Arguments.of("-002017-338", -2017, 338),
        Arguments.of("+002017338", 2017, 338),
        Arguments.of("-002017338", -2017, 338)
    );
  }
  
  protected static Stream<Arguments> weekDates() {
    return Stream.of(
        Arguments.of("2017-W48-1", 2017, 48, 1),
        Arguments.of("2017W481", 2017, 48, 1),
        
        Arguments.of("+002017-W48-1", 2017, 48, 1),
        Arguments.of("-002017-W48-1", -2017, 48, 1),
        Arguments.of("+002017W481", 2017, 48, 1),
        Arguments.of("-002017W481", -2017, 48, 1)
    );
  }
  
  protected static Stream<Arguments> isoTimes() {
    return Stream.of(
        Arguments.of("T123001", 12, 30, 1, "", Optional.empty()),
        Arguments.of("T123001.5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("T123001,5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("T1230", 12, 30, -1, "", Optional.empty()),
        Arguments.of("T1230.5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("T1230,5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("T12", 12, -1, -1, "", Optional.empty()),
        Arguments.of("T12.5", 12, -1, -1, "5", Optional.empty()),
        Arguments.of("T12,5", 12, -1, -1, "5", Optional.empty()),
        Arguments.of("T123001Z", 12, 30, 1, "", Optional.of(0)),
        Arguments.of("T123001+0100", 12, 30, 1, "", Optional.of(1)),
        Arguments.of("T123001-05", 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("12:30:01", 12, 30, 1, "", Optional.empty()),
        Arguments.of("12:30:01.5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("12:30:01,5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("12:30", 12, 30, -1, "", Optional.empty()),
        Arguments.of("12:30.5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("12:30,5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("12:30:01Z", 12, 30, 1, "", Optional.of(0)),
        Arguments.of("12:30:01+01:00", 12, 30, 1, "", Optional.of(1)),
        Arguments.of("12:30:01-05", 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("T12:30:01", 12, 30, 1, "", Optional.empty()),
        Arguments.of("T12:30:01.5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("T12:30:01,5", 12, 30, 1, "5", Optional.empty()),
        Arguments.of("T12:30", 12, 30, -1, "", Optional.empty()),
        Arguments.of("T12:30.5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("T12:30,5", 12, 30, -1, "5", Optional.empty()),
        Arguments.of("T12:30:01Z", 12, 30, 1, "", Optional.of(0)),
        Arguments.of("T12:30:01+01:00", 12, 30, 1, "", Optional.of(1)),
        Arguments.of("T12:30:01-05", 12, 30, 1, "", Optional.of(-5))
    );
  }
  
  protected static Stream<Arguments> calendarDateTimes() {
    return Stream.of(
        Arguments.of("20171204T123001", 2017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("20171204T123001.5", 2017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("20171204T123001,5", 2017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("20171204T1230", 2017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("20171204T1230.5", 2017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("20171204T1230,5", 2017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("20171204T12", 2017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("20171204T12.5", 2017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("20171204T12,5", 2017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("20171204T123001Z", 2017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("20171204T123001+0100", 2017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("20171204T123001-05", 2017, 12, 4, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+1020171204T123001", 102017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+1020171204T123001.5", 102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+1020171204T123001,5", 102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+1020171204T1230", 102017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+1020171204T1230.5", 102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+1020171204T1230,5", 102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+1020171204T12", 102017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+1020171204T12.5", 102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+1020171204T12,5", 102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+1020171204T123001Z", 102017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+1020171204T123001+0100", 102017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+1020171204T123001-05", 102017, 12, 4, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-1020171204T123001", -102017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-1020171204T123001.5", -102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-1020171204T123001,5", -102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-1020171204T1230", -102017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-1020171204T1230.5", -102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-1020171204T1230,5", -102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-1020171204T12", -102017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-1020171204T12.5", -102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-1020171204T12,5", -102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-1020171204T123001Z", -102017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-1020171204T123001+0100", -102017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-1020171204T123001-05", -102017, 12, 4, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("2017-12-04T12:30:01", 2017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("2017-12-04T12:30:01.5", 2017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12:30:01,5", 2017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12:30", 2017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("2017-12-04T12:30.5", 2017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12:30,5", 2017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12", 2017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("2017-12-04T12.5", 2017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12,5", 2017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-12-04T12:30:01Z", 2017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("2017-12-04T12:30:01+01:00", 2017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("2017-12-04T12:30:01-05", 2017, 12, 4, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+102017-12-04T12:30:01", 102017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+102017-12-04T12:30:01.5", 102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12:30:01,5", 102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12:30", 102017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+102017-12-04T12:30.5", 102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12:30,5", 102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12", 102017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+102017-12-04T12.5", 102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12,5", 102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-12-04T12:30:01Z", 102017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+102017-12-04T12:30:01+01:00", 102017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+102017-12-04T12:30:01-05", 102017, 12, 4, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-102017-12-04T12:30:01", -102017, 12, 4, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-102017-12-04T12:30:01.5", -102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12:30:01,5", -102017, 12, 4, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12:30", -102017, 12, 4, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-102017-12-04T12:30.5", -102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12:30,5", -102017, 12, 4, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12", -102017, 12, 4, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-102017-12-04T12.5", -102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12,5", -102017, 12, 4, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-12-04T12:30:01Z", -102017, 12, 4, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-102017-12-04T12:30:01+01:00", -102017, 12, 4, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-102017-12-04T12:30:01-05", -102017, 12, 4, 12, 30, 1, "", Optional.of(-5))
    );
  }
  
  protected static Stream<Arguments> ordinalDateTimes() {
    return Stream.of(
        Arguments.of("2017338T123001", 2017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("2017338T123001.5", 2017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017338T123001,5", 2017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017338T1230", 2017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("2017338T1230.5", 2017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017338T1230,5", 2017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017338T12", 2017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("2017338T12.5", 2017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017338T12,5", 2017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017338T123001Z", 2017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("2017338T123001+0100", 2017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("2017338T123001-05", 2017, 338, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+102017338T123001", 102017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+102017338T123001.5", 102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017338T123001,5", 102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017338T1230", 102017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+102017338T1230.5", 102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017338T1230,5", 102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017338T12", 102017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+102017338T12.5", 102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017338T12,5", 102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017338T123001Z", 102017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+102017338T123001+0100", 102017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+102017338T123001-05", 102017, 338, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-102017338T123001", -102017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-102017338T123001.5", -102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017338T123001,5", -102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017338T1230", -102017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-102017338T1230.5", -102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017338T1230,5", -102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017338T12", -102017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-102017338T12.5", -102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017338T12,5", -102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017338T123001Z", -102017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-102017338T123001+0100", -102017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-102017338T123001-05", -102017, 338, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("2017-338T12:30:01", 2017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("2017-338T12:30:01.5", 2017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-338T12:30:01,5", 2017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-338T12:30", 2017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("2017-338T12:30.5", 2017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-338T12:30,5", 2017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-338T12", 2017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("2017-338T12.5", 2017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-338T12,5", 2017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-338T12:30:01Z", 2017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("2017-338T12:30:01+01:00", 2017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("2017-338T12:30:01-05", 2017, 338, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+102017-338T12:30:01", 102017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+102017-338T12:30:01.5", 102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-338T12:30:01,5", 102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-338T12:30", 102017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+102017-338T12:30.5", 102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-338T12:30,5", 102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-338T12", 102017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+102017-338T12.5", 102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-338T12,5", 102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-338T12:30:01Z", 102017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+102017-338T12:30:01+01:00", 102017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+102017-338T12:30:01-05", 102017, 338, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-102017-338T12:30:01", -102017, 338, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-102017-338T12:30:01.5", -102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-338T12:30:01,5", -102017, 338, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-338T12:30", -102017, 338, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-102017-338T12:30.5", -102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-338T12:30,5", -102017, 338, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-338T12", -102017, 338, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-102017-338T12.5", -102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-338T12,5", -102017, 338, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-338T12:30:01Z", -102017, 338, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-102017-338T12:30:01+01:00", -102017, 338, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-102017-338T12:30:01-05", -102017, 338, 12, 30, 1, "", Optional.of(-5))
    );
  }
  
  protected static Stream<Arguments> weekDateTimes() {
    return Stream.of(
        Arguments.of("2017W481T123001", 2017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("2017W481T123001.5", 2017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017W481T123001,5", 2017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017W481T1230", 2017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("2017W481T1230.5", 2017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017W481T1230,5", 2017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017W481T12", 2017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("2017W481T12.5", 2017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017W481T12,5", 2017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017W481T123001Z", 2017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("2017W481T123001+0100", 2017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("2017W481T123001-05", 2017, 48, 1, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+102017W481T123001", 102017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+102017W481T123001.5", 102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017W481T123001,5", 102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017W481T1230", 102017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+102017W481T1230.5", 102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017W481T1230,5", 102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017W481T12", 102017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+102017W481T12.5", 102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017W481T12,5", 102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017W481T123001Z", 102017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+102017W481T123001+0100", 102017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+102017W481T123001-05", 102017, 48, 1, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-102017W481T123001", -102017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-102017W481T123001.5", -102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017W481T123001,5", -102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017W481T1230", -102017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-102017W481T1230.5", -102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017W481T1230,5", -102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017W481T12", -102017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-102017W481T12.5", -102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017W481T12,5", -102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017W481T123001Z", -102017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-102017W481T123001+0100", -102017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-102017W481T123001-05", -102017, 48, 1, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("2017-W48-1T12:30:01", 2017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("2017-W48-1T12:30:01.5", 2017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12:30:01,5", 2017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12:30", 2017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("2017-W48-1T12:30.5", 2017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12:30,5", 2017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12", 2017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("2017-W48-1T12.5", 2017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12,5", 2017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("2017-W48-1T12:30:01Z", 2017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("2017-W48-1T12:30:01+01:00", 2017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("2017-W48-1T12:30:01-05", 2017, 48, 1, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("+102017-W48-1T12:30:01", 102017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30:01.5", 102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30:01,5", 102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30", 102017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30.5", 102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30,5", 102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12", 102017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("+102017-W48-1T12.5", 102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12,5", 102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("+102017-W48-1T12:30:01Z", 102017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("+102017-W48-1T12:30:01+01:00", 102017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("+102017-W48-1T12:30:01-05", 102017, 48, 1, 12, 30, 1, "", Optional.of(-5)),
        
        Arguments.of("-102017-W48-1T12:30:01", -102017, 48, 1, 12, 30, 1, "", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30:01.5", -102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30:01,5", -102017, 48, 1, 12, 30, 1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30", -102017, 48, 1, 12, 30, -1, "", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30.5", -102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30,5", -102017, 48, 1, 12, 30, -1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12", -102017, 48, 1, 12, -1, -1, "", Optional.empty()),
        Arguments.of("-102017-W48-1T12.5", -102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12,5", -102017, 48, 1, 12, -1, -1, "5", Optional.empty()),
        Arguments.of("-102017-W48-1T12:30:01Z", -102017, 48, 1, 12, 30, 1, "", Optional.of(0)),
        Arguments.of("-102017-W48-1T12:30:01+01:00", -102017, 48, 1, 12, 30, 1, "", Optional.of(1)),
        Arguments.of("-102017-W48-1T12:30:01-05", -102017, 48, 1, 12, 30, 1, "", Optional.of(-5))
    );
  }
  
  protected static Stream<Arguments> fullPeriods() {
    return Stream.of(
        Arguments.of("P2Y3M10DT22H30M15S", 2, 3, 10, 22, 30, 15, ""),
        Arguments.of("P2Y3M10DT22H30M15.5S", 2, 3, 10, 22, 30, 15, "5"),
        Arguments.of("P2Y3M10DT22H30M15,5S", 2, 3, 10, 22, 30, 15, "5"),
        Arguments.of("P2Y3M10D22H30M15S", 2, 3, 10, 22, 30, 15, ""),
        Arguments.of("P2Y3M10D22H30M15.5S", 2, 3, 10, 22, 30, 15, "5"),
        Arguments.of("P2Y3M10D22H30M15,5S", 2, 3, 10, 22, 30, 15, "5"),
        
        Arguments.of("P2Y3M10D", 2, 3, 10, 0, 0, 0, ""),
        Arguments.of("P2Y10D", 2, 0, 10, 0, 0, 0, ""),
        Arguments.of("P3M10D", 0, 3, 10, 0, 0, 0, ""),
        Arguments.of("P2Y", 2, 0, 0, 0, 0, 0, ""),
        Arguments.of("P10D", 0, 0, 10, 0, 0, 0, ""),
        
        Arguments.of("P2Y3M10DT", 2, 3, 10, 0, 0, 0, ""),
        Arguments.of("P2Y3MT", 2, 3, 0, 0, 0, 0, ""),
        Arguments.of("P2Y10DT", 2, 0, 10, 0, 0, 0, ""),
        Arguments.of("P3M10DT", 0, 3, 10, 0, 0, 0, ""),
        Arguments.of("P2YT", 2, 0, 0, 0, 0, 0, ""),
        Arguments.of("P3MT", 0, 3, 0, 0, 0, 0, ""),
        Arguments.of("P10DT", 0, 0, 10, 0, 0, 0, ""),
        
        Arguments.of("P22H30M15S", 0, 0, 0, 22, 30, 15, ""),
        Arguments.of("P22H30M15.5S", 0, 0, 0, 22, 30, 15, "5"),
        Arguments.of("P22H30M15.5S", 0, 0, 0, 22, 30, 15, "5"),
        Arguments.of("P22H30M", 0, 0, 0, 22, 30, 0, ""),
        Arguments.of("P22H15S", 0, 0, 0, 22, 0, 15, ""),
        Arguments.of("P22H15.5S", 0, 0, 0, 22, 0, 15, "5"),
        Arguments.of("P22H15,5S", 0, 0, 0, 22, 0, 15, "5"),
        Arguments.of("P22H", 0, 0, 0, 22, 0, 0, ""),
        Arguments.of("P15S", 0, 0, 0, 0, 0, 15, ""),
        Arguments.of("P15.5S", 0, 0, 0, 0, 0, 15, "5"),
        Arguments.of("P15,5S", 0, 0, 0, 0, 0, 15, "5"),
        
        Arguments.of("PT22H30M15S", 0, 0, 0, 22, 30, 15, ""),
        Arguments.of("PT22H30M15.5S", 0, 0, 0, 22, 30, 15, "5"),
        Arguments.of("PT22H30M15.5S", 0, 0, 0, 22, 30, 15, "5"),
        Arguments.of("PT22H30M", 0, 0, 0, 22, 30, 0, ""),
        Arguments.of("PT22H15S", 0, 0, 0, 22, 0, 15, ""),
        Arguments.of("PT22H15.5S", 0, 0, 0, 22, 0, 15, "5"),
        Arguments.of("PT22H15,5S", 0, 0, 0, 22, 0, 15, "5"),
        Arguments.of("PT30M15S", 0, 0, 0, 0, 30, 15, ""),
        Arguments.of("PT30M15.5S", 0, 0, 0, 0, 30, 15, "5"),
        Arguments.of("PT30M15,5S", 0, 0, 0, 0, 30, 15, "5"),
        Arguments.of("PT22H", 0, 0, 0, 22, 0, 0, ""),
        Arguments.of("PT30M", 0, 0, 0, 0, 30, 0, ""),
        Arguments.of("PT15S", 0, 0, 0, 0, 0, 15, ""),
        Arguments.of("PT15.5S", 0, 0, 0, 0, 0, 15, "5"),
        Arguments.of("PT15,5S", 0, 0, 0, 0, 0, 15, "5"),
        
        Arguments.of("PT30.5M", 0, 0, 0, 0, 30, 0, "5"),
        Arguments.of("PT30,5M", 0, 0, 0, 0, 30, 0, "5"),
        Arguments.of("PT22.5H", 0, 0, 0, 22, 0, 0, "5"),
        Arguments.of("PT22,5H", 0, 0, 0, 22, 0, 0, "5"),
        Arguments.of("PT22H30.5M", 0, 0, 0, 22, 30, 0, "5"),
        Arguments.of("PT22H30,5M", 0, 0, 0, 22, 30, 0, "5"),
        Arguments.of("P22.5H", 0, 0, 0, 22, 0, 0, "5"),
        Arguments.of("P22,5H", 0, 0, 0, 22, 0, 0, "5"),
        Arguments.of("P22H30.5M", 0, 0, 0, 22, 30, 0, "5"),
        Arguments.of("P22H30,5M", 0, 0, 0, 22, 30, 0, "5")
    );
  }
  
  protected static Stream<Arguments> weekPeriods() {
    return Stream.of(
        Arguments.of("P4W", 4)
    );
  }
  
  protected static Stream<Arguments> invalidPeriods() {
    return Stream.of(
        // At least one numbered field needs to be specified
        Arguments.of("P"),
        Arguments.of("PT"),
        
        // Fields must be in correct order
        Arguments.of("P10D2Y"),
        Arguments.of("P22H10D"),
        Arguments.of("P15S30M"),
        
        // Week periods must appear alone
        Arguments.of("P2Y4W"),
        Arguments.of("P4W1D"),
        Arguments.of("P4W15S"),
        
        // Only the seconds field is allowed to have a fractional part
        Arguments.of("P10.5DT"),
        Arguments.of("P10,5DT"),
        Arguments.of("P3.5MT"),
        Arguments.of("P3,5MT"),
        Arguments.of("P2.5YT"),
        Arguments.of("P2,5YT"),
        Arguments.of("P3M10.5DT"),
        Arguments.of("P3M10,5DT"),
        Arguments.of("P2Y10.5DT"),
        Arguments.of("P2Y10,5DT"),
        Arguments.of("P2Y3.5MT"),
        Arguments.of("P2Y3,5MT"),
        Arguments.of("P2Y3M10.5DT"),
        Arguments.of("P2Y3M10,5DT"),
        Arguments.of("P10.5D"),
        Arguments.of("P10,5D"),
        Arguments.of("P2.5Y"),
        Arguments.of("P2,5Y"),
        Arguments.of("P3M10.5D"),
        Arguments.of("P3M10,5D"),
        Arguments.of("P2Y10.5D"),
        Arguments.of("P2Y10,5D"),
        Arguments.of("P2Y3M10.5D"),
        Arguments.of("P2Y3M10,5D"),
        
        Arguments.of("P4.5W"),
        Arguments.of("P4,5W")
    );
  }
  
  protected static Stream<Arguments> numericDates() {
    return Stream.of(
        Arguments.of("04.12.2017", 2017, 12, 4),
        Arguments.of("12.2017", 2017, 12, -1),
        Arguments.of("2017", 2017, -1, -1)
    );
  }
  
  protected static Stream<Arguments> numericDatesWithoutYear() {
    return numericDates().filter((args) -> ((String) args.get()[0]).contains("."));
  }
  
  protected static Stream<Arguments> alphanumericDates() {
    return Stream.of(
        Arguments.of("4. Januar 2017", 2017, 1, 4),
        Arguments.of("4. Februar 2017", 2017, 2, 4),
        Arguments.of("4. März 2017", 2017, 3, 4),
        Arguments.of("4. April 2017", 2017, 4, 4),
        Arguments.of("4. Mai 2017", 2017, 5, 4),
        Arguments.of("4. Juni 2017", 2017, 6, 4),
        Arguments.of("4. Juli 2017", 2017, 7, 4),
        Arguments.of("4. August 2017", 2017, 8, 4),
        Arguments.of("4. September 2017", 2017, 9, 4),
        Arguments.of("4. Oktober 2017", 2017, 10, 4),
        Arguments.of("4. November 2017", 2017, 11, 4),
        Arguments.of("4. Dezember 2017", 2017, 12, 4),
        
        Arguments.of("4. Jan. 2017", 2017, 1, 4),
        Arguments.of("4. Feb. 2017", 2017, 2, 4),
        Arguments.of("4. Mär. 2017", 2017, 3, 4),
        Arguments.of("4. Apr. 2017", 2017, 4, 4),
        Arguments.of("4. Mai. 2017", 2017, 5, 4),
        Arguments.of("4. Jun. 2017", 2017, 6, 4),
        Arguments.of("4. Jul. 2017", 2017, 7, 4),
        Arguments.of("4. Aug. 2017", 2017, 8, 4),
        Arguments.of("4. Sep. 2017", 2017, 9, 4),
        Arguments.of("4. Okt. 2017", 2017, 10, 4),
        Arguments.of("4. Nov. 2017", 2017, 11, 4),
        Arguments.of("4. Dez. 2017", 2017, 12, 4),
        
        Arguments.of("Januar 2017", 2017, 1, -1),
        Arguments.of("Februar 2017", 2017, 2, -1),
        Arguments.of("März 2017", 2017, 3, -1),
        Arguments.of("April 2017", 2017, 4, -1),
        Arguments.of("Mai 2017", 2017, 5, -1),
        Arguments.of("Juni 2017", 2017, 6, -1),
        Arguments.of("Juli 2017", 2017, 7, -1),
        Arguments.of("August 2017", 2017, 8, -1),
        Arguments.of("September 2017", 2017, 9, -1),
        Arguments.of("Oktober 2017", 2017, 10, -1),
        Arguments.of("November 2017", 2017, 11, -1),
        Arguments.of("Dezember 2017", 2017, 12, -1),
        
        Arguments.of("Jan. 2017", 2017, 1, -1),
        Arguments.of("Feb. 2017", 2017, 2, -1),
        Arguments.of("Mär. 2017", 2017, 3, -1),
        Arguments.of("Apr. 2017", 2017, 4, -1),
        Arguments.of("Mai. 2017", 2017, 5, -1),
        Arguments.of("Jun. 2017", 2017, 6, -1),
        Arguments.of("Jul. 2017", 2017, 7, -1),
        Arguments.of("Aug. 2017", 2017, 8, -1),
        Arguments.of("Sep. 2017", 2017, 9, -1),
        Arguments.of("Okt. 2017", 2017, 10, -1),
        Arguments.of("Nov. 2017", 2017, 11, -1),
        Arguments.of("Dez. 2017", 2017, 12, -1)
    );
  }
  
  protected static Stream<Arguments> germanDates() {
    return Stream.concat(numericDates(), alphanumericDates());
  }
  
  protected static Stream<Arguments> germanTimes() {
    return Stream.of(
        Arguments.of("12:30:01 Uhr", 12, 30, 1),
        Arguments.of("12:30 Uhr", 12, 30, -1),
        Arguments.of("12 Uhr", 12, -1, -1),
        Arguments.of("06:30:01 Uhr", 6, 30, 1),
        Arguments.of("06:30 Uhr", 6, 30, -1),
        Arguments.of("6 Uhr", 6, -1, -1)
    );
  }
  
  protected static Stream<Arguments> germanDateTimes() {
    return Stream.of(
        Arguments.of("04.12.2017 12:30:01 Uhr", 2017, 12, 4, 12, 30, 1),
        Arguments.of("04.12.2017 12:30 Uhr", 2017, 12, 4, 12, 30, -1),
        Arguments.of("04.12.2017 12 Uhr", 2017, 12, 4, 12, -1, -1),
        Arguments.of("04.12.2017 06:30:01 Uhr", 2017, 12, 4, 6, 30, 1),
        Arguments.of("04.12.2017 06:30 Uhr", 2017, 12, 4, 6, 30, -1),
        Arguments.of("04.12.2017 6 Uhr", 2017, 12, 4, 6, -1, -1),
        
        Arguments.of("4. Januar 2017 12:30:01 Uhr", 2017, 1, 4, 12, 30, 1),
        Arguments.of("4. Januar 2017 12:30 Uhr", 2017, 1, 4, 12, 30, -1),
        Arguments.of("4. Januar 2017 12 Uhr", 2017, 1, 4, 12, -1, -1),
        Arguments.of("4. Januar 2017 06:30:01 Uhr", 2017, 1, 4, 6, 30, 1),
        Arguments.of("4. Januar 2017 06:30 Uhr", 2017, 1, 4, 6, 30, -1),
        Arguments.of("4. Januar 2017 6 Uhr", 2017, 1, 4, 6, -1, -1),
        
        Arguments.of("4. Jan. 2017 12:30:01 Uhr", 2017, 1, 4, 12, 30, 1),
        Arguments.of("4. Jan. 2017 12:30 Uhr", 2017, 1, 4, 12, 30, -1),
        Arguments.of("4. Jan. 2017 12 Uhr", 2017, 1, 4, 12, -1, -1),
        Arguments.of("4. Jan. 2017 06:30:01 Uhr", 2017, 1, 4, 6, 30, 1),
        Arguments.of("4. Jan. 2017 06:30 Uhr", 2017, 1, 4, 6, 30, -1),
        Arguments.of("4. Jan. 2017 6 Uhr", 2017, 1, 4, 6, -1, -1),
        
        Arguments.of("4. März 2017 12:30:01 Uhr", 2017, 3, 4, 12, 30, 1),
        Arguments.of("4. März 2017 12:30 Uhr", 2017, 3, 4, 12, 30, -1),
        Arguments.of("4. März 2017 12 Uhr", 2017, 3, 4, 12, -1, -1),
        Arguments.of("4. März 2017 06:30:01 Uhr", 2017, 3, 4, 6, 30, 1),
        Arguments.of("4. März 2017 06:30 Uhr", 2017, 3, 4, 6, 30, -1),
        Arguments.of("4. März 2017 6 Uhr", 2017, 3, 4, 6, -1, -1),
        
        Arguments.of("4. Mär. 2017 12:30:01 Uhr", 2017, 3, 4, 12, 30, 1),
        Arguments.of("4. Mär. 2017 12:30 Uhr", 2017, 3, 4, 12, 30, -1),
        Arguments.of("4. Mär. 2017 12 Uhr", 2017, 3, 4, 12, -1, -1),
        Arguments.of("4. Mär. 2017 06:30:01 Uhr", 2017, 3, 4, 6, 30, 1),
        Arguments.of("4. Mär. 2017 06:30 Uhr", 2017, 3, 4, 6, 30, -1),
        Arguments.of("4. Mär. 2017 6 Uhr", 2017, 3, 4, 6, -1, -1)
    );
  }
}
