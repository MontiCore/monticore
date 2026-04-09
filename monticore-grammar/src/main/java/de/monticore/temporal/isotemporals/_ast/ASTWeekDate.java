package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.WeekFields;

public interface ASTWeekDate extends ASTWeekDateTOP {
  
  TemporalField WEEK_OF_YEAR = WeekFields.ISO.weekOfYear();
  
  int getYear();
  
  int getWeek();
  
  boolean isPresentDayOfWeek();
  
  int getDayOfWeek();
  
  @Override
  default boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return true;
        case DAY_OF_WEEK:
          return isPresentDayOfWeek();
      }
    }
    return field == WEEK_OF_YEAR;
  }
  
  @Override
  default long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return getYear();
        case DAY_OF_WEEK:
          return getDayOfWeek();
      }
    } else if (field == WEEK_OF_YEAR) {
      return getWeek();
    }
    
    throw new UnsupportedTemporalTypeException(field.toString());
  }

  // Normally, the default implementation takes care of converting the result of getLong to int, but
  // fetching the WEEK_OF_YEAR would then cause an error. This implementation is still sound, as each
  // supported field has a range which fits in an int, anyway.
  @Override
  default int get(TemporalField field) {
    return (int) getLong(field);
  }
}
