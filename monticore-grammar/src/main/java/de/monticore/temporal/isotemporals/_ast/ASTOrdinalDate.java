package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTOrdinalDate extends ASTOrdinalDateTOP {
  
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
        case DAY_OF_YEAR:
          return true;
      }
    }
    return false;
  }
  
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return getYear();
        case DAY_OF_YEAR:
          return getDayOfYear();
      }
    }
    
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
