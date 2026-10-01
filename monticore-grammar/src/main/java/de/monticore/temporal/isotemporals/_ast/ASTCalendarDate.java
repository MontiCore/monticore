/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTCalendarDate extends ASTCalendarDateTOP {
  
  /**
   * @return a string representing the CalendarDate that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentSign()) {
      if (getSign() == ASTSign.PLUS) {
        result.append("+");
      }
      else {
        result.append("-");
      }
    }
    result.append(getPre());
    if (isPresentMid()) {
      result.append("-");
      result.append(getMid());
    }
    if (isPresentPost()) {
      result.append("-");
      result.append(getPost());
    }
    return result.toString();
  }
  
  /**
   * Checks whether the specified temporal field is supported by this calendar date.
   * <p>
   * Only the standard calendar fields {@link ChronoField#YEAR},
   * {@link ChronoField#MONTH_OF_YEAR}, and {@link ChronoField#DAY_OF_MONTH}
   * are supported, and only if the corresponding value is present.
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return isPresentYear();
        case MONTH_OF_YEAR:
          return isPresentMonth();
        case DAY_OF_MONTH:
          return isPresentDay();
      }
    }
    return false;
  }
  
  /**
   * Returns the value of the specified temporal field.
   *
   * @param field the temporal field to query
   * @return the value of the requested field
   * @throws UnsupportedTemporalTypeException if the field is not supported
   * by this calendar date
   */
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField f) {
      switch (f) {
        case YEAR:
          return getYear();
        case MONTH_OF_YEAR:
          return getMonth();
        case DAY_OF_MONTH:
          return getDay();
      }
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
