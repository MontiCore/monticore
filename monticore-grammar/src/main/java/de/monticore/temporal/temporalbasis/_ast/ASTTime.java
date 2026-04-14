/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis._ast;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.time.temporal.UnsupportedTemporalTypeException;

import static java.time.temporal.ChronoField.*;

@SuppressWarnings("unused") // This interface is part of the public-facing API
public interface ASTTime extends ASTTimeTOP {

  @Override
  default Temporal toTemporal() {
    if (isExactlyLocalTime()) {
      return toLocalTime();
    } else if (isOffsetTime()) {
      return toOffsetTime();
    }
    throw new UnsupportedTemporalTypeException("Conversion of ASTTime only supported into LocalTime or OffsetTime");
  }

  default boolean isLocalTime() {
    return isSupported(MINUTE_OF_HOUR) && isSupported(SECOND_OF_MINUTE);
  }
  
  default boolean isExactlyLocalTime() {
    return isLocalTime() && !isSupported(OFFSET_SECONDS);
  }
  
  default LocalTime toLocalTime() {
    int hour;
    if (isSupported(HOUR_OF_DAY)) {
      hour = get(HOUR_OF_DAY);
    } else {
      hour = 12 * get(AMPM_OF_DAY) + get(HOUR_OF_AMPM);
    }
    
    int nano = 0;
    if (isSupported(NANO_OF_SECOND)) {
      nano = get(NANO_OF_SECOND);
    }
    return LocalTime.of(hour, get(MINUTE_OF_HOUR), get(SECOND_OF_MINUTE), nano);
  }
  
  default boolean isOffsetTime() {
    return isLocalTime() && isSupported(OFFSET_SECONDS);
  }
  
  default OffsetTime toOffsetTime() {
    return toLocalTime().atOffset(ZoneOffset.ofTotalSeconds(get(OFFSET_SECONDS)));
  }
  
  
}
