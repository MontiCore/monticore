/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTISOTime extends ASTISOTimeTOP {
  
  /**
 * Returns the raw ISO time string representation of this node.
 * <p>
 * The format is constructed from available components in the following order:
 * pre-time part (or source token), optional minutes, optional seconds,
 * optional fractional seconds, and optional time shift.
 *
 * @return the raw ISO time string
 */
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentPre()) {
      result.append(getPre());
    } else {
      result.append(getPreWithT().getSource());
    }
    if (isPresentMid()) {
      result.append(':');
      result.append(getMid());
    }
    if (isPresentPost()) {
      result.append(':');
      result.append(getPost());
    }
    if (isPresentFraction()) {
      result.append(getFraction().toRawString());
    }
    if (isPresentTimeShiftSource()) {
      result.append(getTimeShiftSource().toRawString());
    }
    return result.toString();
  }
  
  /**
   * Computes the nanosecond-of-second value from the fractional second digits.
   * <p>
   * The value is derived from the first up to 9 decimal digits of the fraction
   * and padded with zeros if necessary.
   *
   * @return the nanosecond-of-second value
   * @throws UnsupportedTemporalTypeException if the second component is not present
   */
  public int getNanoOfSecond() {
    if (!isPresentSecond()) {
      throw new UnsupportedTemporalTypeException("Second is required to compute nanoseconds");
    }
    String nanoOfSecond =
        getDecimalDigits().substring(0, Integer.min(getDecimalDigits().length(), 9));
    if (nanoOfSecond.length() < 9) {
      String toAppend = "0".repeat(9 - nanoOfSecond.length());
      nanoOfSecond += toAppend;
    }
    return Integer.parseInt(nanoOfSecond);
  }
  
  /**
   * Checks whether the specified temporal field is supported by this time.
   * <p>
   * Supported fields depend on the available time components:
   * <ul>
   *   <li>{@link ChronoField#HOUR_OF_DAY} is always supported</li>
   *   <li>{@link ChronoField#MINUTE_OF_HOUR} if a minute is present</li>
   *   <li>{@link ChronoField#SECOND_OF_MINUTE} if a second is present</li>
   *   <li>{@link ChronoField#NANO_OF_SECOND} if fractional seconds are present</li>
   *   <li>{@link ChronoField#OFFSET_SECONDS} if a time shift is present</li>
   * </ul>
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case HOUR_OF_DAY:
          return true;
        case MINUTE_OF_HOUR:
          return isPresentMinute();
        case SECOND_OF_MINUTE:
          return isPresentSecond();
        case NANO_OF_SECOND:
          return isPresentSecond() && isPresentDecimalDigits();
        case OFFSET_SECONDS:
          return isPresentTimeShift();
      }
    }
    return false;
  }
  
  /**
   * Returns the value of the specified temporal field.
   * <p>
   * The value is computed from the corresponding time component.
   *
   * @param field the temporal field to query
   * @return the value of the requested field
   * @throws UnsupportedTemporalTypeException if the field is not supported
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
        case NANO_OF_SECOND:
          return getNanoOfSecond();
        case OFFSET_SECONDS:
          // Time shift is in hours but result has to be in seconds
          return 3600L * getTimeShiftHour() + 60L * getTimeShiftMinute();
      }
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }
  
  public boolean isPresentTimeShift() {
    return this.timeShiftHour.isPresent() || this.timeShiftMinute.isPresent();
  }
}
