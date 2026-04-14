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
      }
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
