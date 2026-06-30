/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public interface ASTDEDate extends ASTDEDateTOP {
  
  int getYear();
  
  boolean isPresentMonth();
  
  int getMonth();
  
  boolean isPresentDay();
  
  int getDay();
  
  /**
   * Checks whether the specified temporal field is supported by this date.
   * <p>
   * {@link ChronoField#YEAR} is always supported. {@link ChronoField#MONTH_OF_YEAR}
   * and {@link ChronoField#DAY_OF_MONTH} are supported only if the corresponding
   * value is present.
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  default boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return true;
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
   * @throws UnsupportedTemporalTypeException if the field is not supported by
   * this date
   */
  @Override
  default long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
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
