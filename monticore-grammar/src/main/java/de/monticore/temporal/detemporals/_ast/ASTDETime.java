/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

import static de.monticore.literals.MCLiteralsDecoder.decodeNat;

public class ASTDETime extends ASTDETimeTOP {

  public int getHour() {
    return decodeNat(getHourSource());
  }

  public boolean isPresentMinute() {
    return isPresentMinuteSource();
  }

  public int getMinute() {
    return decodeNat(getMinuteSource());
  }

  public boolean isPresentSecond() {
    return isPresentSecondSource();
  }

  public int getSecond() {
    return decodeNat(getSecondSource());
  }
  
  /**
   * Checks whether the specified temporal field is supported by this time.
   * <p>
   * Supported fields are:
   * <ul>
   *   <li>{@link ChronoField#HOUR_OF_DAY} (always supported)</li>
   *   <li>{@link ChronoField#MINUTE_OF_HOUR} (if a minute is present)</li>
   *   <li>{@link ChronoField#SECOND_OF_MINUTE} (if a second is present)</li>
   * </ul>
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField f) {
      switch (f) {
        case HOUR_OF_DAY:
          return true;
        case MINUTE_OF_HOUR:
          return isPresentMinute();
        case SECOND_OF_MINUTE:
          return isPresentSecond();
      }
    }
    return false;
  }
  
  /**
   * Returns the value of the specified temporal field.
   *
   * @param field the temporal field to query
   * @return the value of the requested field
   * @throws UnsupportedTemporalTypeException if the field is not supported by
   * this date
   */
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField f) {
      switch (f) {
        case HOUR_OF_DAY:
          return getHour();
        case MINUTE_OF_HOUR:
          return getMinute();
        case SECOND_OF_MINUTE:
          return getSecond();
      }
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
