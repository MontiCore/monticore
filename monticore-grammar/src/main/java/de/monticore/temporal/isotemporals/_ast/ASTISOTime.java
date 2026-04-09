package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTISOTime extends ASTISOTimeTOP {
  
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
  
  public int getNanoOfSecond() {
    assert isPresentSecond();
    String nanoOfSecond = getDecimalDigits().substring(0, Integer.min(getDecimalDigits().length(), 9));
    if (nanoOfSecond.length() < 9) {
      String toAppend = "0".repeat(9 - nanoOfSecond.length());
      nanoOfSecond += toAppend;
    }
    return Integer.parseInt(nanoOfSecond);
  }
  
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
  
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
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
          return 3600L * getTimeShift();  // Time shift is in hours but result has to be in seconds
      }
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
