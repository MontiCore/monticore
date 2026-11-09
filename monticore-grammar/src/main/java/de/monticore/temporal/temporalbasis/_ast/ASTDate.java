/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis._ast;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.time.temporal.UnsupportedTemporalTypeException;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.IsoFields.WEEK_BASED_YEAR;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;

@SuppressWarnings("unused") // This interface is part of the public-facing API
public interface ASTDate extends ASTDateTOP {
  
  /**
   * Converts this AST date to the corresponding {@link Temporal}.
   *
   * <p>The result depends on the available fields:
   * <ul>
   *   <li>{@link LocalDate} if a full calendar date is present</li>
   *   <li>{@link YearMonth} if only year-month precision is available</li>
   *   <li>{@link Year} if only year precision is available</li>
   * </ul>
   *
   * @return the corresponding temporal representation
   * @throws UnsupportedTemporalTypeException if no valid conversion is possible
   * @throws DateTimeException if a field value is out of range,
   * e.g., month 13 or week 53 in a year with 52 ISO weeks
   */
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
  
  /**
   * Checks whether this date represents a full {@link LocalDate}.
   * <p>
   * A local date may be represented in one of the following forms:
   * <ul>
   *   <li>yyyy-MM-dd</li>
   *   <li>yyyy-DDD (day-of-year)</li>
   *   <li>ISO week date (yyyy-ww-d)</li>
   * </ul>
   *
   * @return {@code true} if this can be represented as a {@link LocalDate}
   */
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
    return isSupported(WEEK_BASED_YEAR) && isSupported(WEEK_OF_WEEK_BASED_YEAR) && isSupported(DAY_OF_WEEK);
  }
  
  /**
   * Converts this AST date to a {@link LocalDate}.
   * <p>
   * The conversion depends on the available temporal fields and supports:
   * <ul>
   *   <li>Year-month-day representation</li>
   *   <li>Year-day-of-year representation</li>
   *   <li>ISO week date representation</li>
   * </ul>
   *
   * @return the corresponding {@link LocalDate}
   * @throws UnsupportedTemporalTypeException if required fields are missing
   * @throws DateTimeException if a field value is out of range,
   * e.g., month 13 or week 53 in a year with 52 ISO weeks
   */
  default LocalDate toLocalDate() {
    // In case it has
    if (isSupported(YEAR) && isSupported(MONTH_OF_YEAR) && isSupported(DAY_OF_MONTH)) {
      return LocalDate.of(get(YEAR), get(MONTH_OF_YEAR), get(DAY_OF_MONTH));
    }
    
    if (isSupported(YEAR) && isSupported(DAY_OF_YEAR)) {
      return LocalDate.ofYearDay(get(YEAR), get(DAY_OF_YEAR));
    }
    
    if (isSupported(WEEK_BASED_YEAR) && isSupported(WEEK_OF_WEEK_BASED_YEAR) && isSupported(DAY_OF_WEEK)) {
      // 4 January always lies in week 1. The adjuster only checks the range 1-53, so get() has to
      // check the week against the range of the actual week-based year.
      return LocalDate.of(get(WEEK_BASED_YEAR), 1, 4)
          .with(WEEK_OF_WEEK_BASED_YEAR, get(WEEK_OF_WEEK_BASED_YEAR))
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
        && !isSupported(WEEK_OF_WEEK_BASED_YEAR);
  }
  
  default Year toYear() {
    return Year.of(get(YEAR));
  }
}
