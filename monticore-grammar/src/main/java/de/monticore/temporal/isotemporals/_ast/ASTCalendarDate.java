package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTCalendarDate extends ASTCalendarDateTOP {
  
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentSign()) {
      if (getSign() == ASTSign.PLUS) {
        result.append("+");
      } else {
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
    // TODO Handle custom temporal fields CENTURY and DECADE
    return false;
  }
  
  @Override
  public long getLong(TemporalField field) {
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
    // TODO Handle custom temporal fields CENTURY and DECADE
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
