/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis._ast;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.WeekFields;

import static java.time.temporal.ChronoField.*;

@SuppressWarnings("unused") // This interface is part of the public-facing API
public interface ASTDate extends ASTDateTOP {

  @Override
  default Temporal toTemporal() {
    if (isLocalDate()) {
      return toLocalDate();
    } else if (isExactlyYearMonth()) {
      return toYearMonth();
    } else if (isExactlyYear()) {
      return toYear();
    }
    throw new UnsupportedTemporalTypeException("Conversion of ASTDate only supported into LocalDate, YearMonth, or Year");
  }

  default boolean isLocalDate() {
    // Check whether it has form yyyy-mm-dd
    if (isSupported(YEAR) && isSupported(MONTH_OF_YEAR) && isSupported(DAY_OF_MONTH)) {
      return true;
    }
    
    // Check whether it has form yyyy-ddd
    if (isSupported(YEAR) && isSupported(DAY_OF_YEAR)) {
      return true;
    }
    
    // Check whether it has form yyyy-ww-d
    return isSupported(YEAR) && isSupported(WeekFields.ISO.weekOfYear()) && isSupported(DAY_OF_WEEK);
  }
  
  default LocalDate toLocalDate() {
    // In case it has
    if (isSupported(YEAR) && isSupported(MONTH_OF_YEAR) && isSupported(DAY_OF_MONTH)) {
      return LocalDate.of(get(YEAR), get(MONTH_OF_YEAR), get(DAY_OF_MONTH));
    }
    
    if (isSupported(YEAR) && isSupported(DAY_OF_YEAR)) {
      return LocalDate.ofYearDay(get(YEAR), get(DAY_OF_YEAR));
    }
    
    if (isSupported(YEAR) && isSupported(WeekFields.ISO.weekOfYear()) && isSupported(DAY_OF_WEEK)) {
      return LocalDate.ofYearDay(get(YEAR), 1)
          .plusWeeks(get(WeekFields.ISO.weekOfYear()) - 1)
          .with(DAY_OF_WEEK, get(DAY_OF_WEEK));
    }
    
    throw new UnsupportedTemporalTypeException("ASTDate cannot be converted to LocalDate: Some temporal fields are missing");
  }
  
  default boolean isYearMonth() {
    return isSupported(YEAR) && isSupported(MONTH_OF_YEAR);
  }
  
  default boolean isExactlyYearMonth() {
    return isYearMonth() && !isSupported(DAY_OF_MONTH);
  }
  
  default YearMonth toYearMonth() {
    return YearMonth.of(get(YEAR), get(MONTH_OF_YEAR));
  }
  
  default boolean isYear() {
    return isSupported(YEAR);
  }
  
  default boolean isExactlyYear() {
    return isYear() && !isSupported(DAY_OF_YEAR) && !isSupported(MONTH_OF_YEAR)
        && !isSupported(WeekFields.ISO.weekOfYear());
  }
  
  default Year toYear() {
    return Year.of(get(YEAR));
  }
}
