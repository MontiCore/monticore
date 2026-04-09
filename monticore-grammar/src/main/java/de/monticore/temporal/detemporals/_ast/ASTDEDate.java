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
