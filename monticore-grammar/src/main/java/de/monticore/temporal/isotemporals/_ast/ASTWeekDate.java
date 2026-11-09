/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;

/**
 * An ISO 8601 week date, e.g., {@code 2019-W01-1}.
 * <p>
 * A week date names an ISO week-based year, an ISO week and optionally a day of
 * the week. The ISO week-based year differs from the calendar year around 1 January:
 * <ul>
 *   <li>{@code 2019-W01-1} is Monday, 2018-12-31: week-based year 2019, week 1,
 *   calendar year 2018</li>
 *   <li>{@code 2020-W53-5} is Friday, 2021-01-01: week-based year 2020, week 53,
 *   calendar year 2021</li>
 * </ul>
 * As a {@link java.time.temporal.TemporalAccessor}, a week date supports these fields:
 * <table>
 *   <caption>Supported fields</caption>
 *   <tr><th>Field</th><th>Value</th><th>Supported</th></tr>
 *   <tr><td>{@link IsoFields#WEEK_BASED_YEAR}</td><td>ISO week-based year ({@link #getWeekBasedYear()})</td>
 *   <td>always</td></tr>
 *   <tr><td>{@link IsoFields#WEEK_OF_WEEK_BASED_YEAR}</td><td>ISO week ({@link #getWeekOfWeekBasedYear()})</td>
 *   <td>always</td></tr>
 *   <tr><td>{@link ChronoField#DAY_OF_WEEK}</td><td>day of the week ({@link #getDayOfWeek()})</td>
 *   <td>with day of the week</td></tr>
 *   <tr><td>{@link ChronoField#YEAR}</td><td>calendar year of the date, not the week-based year
 *   ({@link #getCalendarYear()})</td><td>with day of the week</td></tr>
 *   <tr><td>{@link #WEEK_OF_YEAR}</td><td>week of the calendar year of the date, not the ISO week
 *   </td><td>with day of the week</td></tr>
 * </table>
 * Use {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} for the ISO week. {@link #WEEK_OF_YEAR} counts
 * weeks within the calendar year and differs from the ISO week around 1 January (e.g., week 53
 * for 2018-12-31 and week 0 for 2021-01-01).
 */
public interface ASTWeekDate extends ASTWeekDateTOP {

  /**
   * {@code WeekFields.ISO.weekOfYear()}: the week of the calendar year of the date (0-53), where
   * weeks start on Monday and week 1 is the first week with at least four days in the calendar
   * year. Days before week 1 lie in week 0.
   * <p>
   * This is not the ISO week of this week date, e.g., week 53 for {@code 2019-W01-1}
   * (2018-12-31) and week 0 for {@code 2020-W53-5} (2021-01-01). Use
   * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} for the ISO week.
   */
  TemporalField WEEK_OF_YEAR = WeekFields.ISO.weekOfYear();

  /**
   * Returns the ISO week-based year, i.e., the value of {@link IsoFields#WEEK_BASED_YEAR}.
   * <p>
   * This is not necessarily the calendar year of this date, e.g., 2019 for {@code 2019-W01-1},
   * which is 2018-12-31. Use {@code get(ChronoField.YEAR)} for the calendar year.
   *
   * @return the ISO week-based year
   */
  int getWeekBasedYear();

  /**
   * Returns the ISO week of the week-based year, i.e., the value of
   * {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR}.
   * <p>
   * The value is not validated. It should lie in {@code range(IsoFields.WEEK_OF_WEEK_BASED_YEAR)},
   * i.e., 1-52 or 1-53 depending on the week-based year.
   *
   * @return the ISO week of the week-based year
   */
  int getWeekOfWeekBasedYear();

  /**
   * @return {@code true} if this week date has a day of the week, e.g., {@code 2019-W01-1}, and
   * {@code false} if it denotes a whole week, e.g., {@code 2019-W01}
   */
  boolean isPresentDayOfWeek();

  /**
   * Returns the ISO day of the week, i.e., the value of {@link ChronoField#DAY_OF_WEEK}, from 1
   * (Monday) to 7 (Sunday).
   *
   * @return the ISO day of the week
   * @throws IllegalStateException if no day of the week is present
   * @see #isPresentDayOfWeek()
   */
  int getDayOfWeek();

  /**
   * @return the day of the week as {@link DayOfWeek}
   * @throws IllegalStateException if no day of the week is present
   * @throws java.time.DateTimeException if the day of the week is not between 1 and 7
   * @see #getDayOfWeek()
   */
  default DayOfWeek toDayOfWeek() {
    return DayOfWeek.of(getDayOfWeek());
  }

  /**
   * Returns the calendar year of this date, i.e., the value of {@link ChronoField#YEAR}.
   * <p>
   * This is not necessarily the week-based year, e.g., 2018 for {@code 2019-W01-1}, which is
   * 2018-12-31.
   *
   * @return the calendar year of this date
   * @throws UnsupportedTemporalTypeException if no day of the week is present, as the calendar
   * year can only be derived from a full date
   * @throws java.time.DateTimeException if the week is out of range for the week-based year
   */
  default int getCalendarYear() {
    return toLocalDate().getYear();
  }

  /**
   * Checks whether the specified temporal field is supported by this week date.
   * <p>
   * {@link IsoFields#WEEK_BASED_YEAR} and {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} are always
   * supported. {@link ChronoField#DAY_OF_WEEK}, {@link ChronoField#YEAR} and {@link #WEEK_OF_YEAR}
   * are supported only if a day of the week is present, as the calendar year and its weeks can only
   * be derived from a full date.
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  default boolean isSupported(TemporalField field) {
    if (field == ChronoField.DAY_OF_WEEK || field == ChronoField.YEAR || field == WEEK_OF_YEAR) {
      return isPresentDayOfWeek();
    }
    return field == IsoFields.WEEK_BASED_YEAR || field == IsoFields.WEEK_OF_WEEK_BASED_YEAR;
  }

  /**
   * Returns the value of the specified temporal field.
   * <p>
   * {@link ChronoField#YEAR} returns the calendar year of the date, e.g., 2018 for
   * {@code 2019-W01-1}. Use {@link IsoFields#WEEK_BASED_YEAR} for the week-based year.
   * {@link #WEEK_OF_YEAR} returns the week of the calendar year, e.g., 53 for {@code 2019-W01-1}.
   * Use {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} for the ISO week.
   *
   * @param field the temporal field to query
   * @return the value of the requested field
   * @throws UnsupportedTemporalTypeException if the field is not supported by
   * this week date
   * @throws java.time.DateTimeException if {@link ChronoField#YEAR} or {@link #WEEK_OF_YEAR} is
   * requested and the week is out of range for the week-based year
   */
  @Override
  default long getLong(TemporalField field) {
    if (field == ChronoField.DAY_OF_WEEK && isPresentDayOfWeek()) {
      return getDayOfWeek();
    } else if (field == ChronoField.YEAR && isPresentDayOfWeek()) {
      return getCalendarYear();
    } else if (field == WEEK_OF_YEAR && isPresentDayOfWeek()) {
      return toLocalDate().get(WEEK_OF_YEAR);
    } else if (field == IsoFields.WEEK_BASED_YEAR) {
      return getWeekBasedYear();
    } else if (field == IsoFields.WEEK_OF_WEEK_BASED_YEAR) {
      return getWeekOfWeekBasedYear();
    }

    throw new UnsupportedTemporalTypeException(field.toString());
  }

  /**
   * Returns the range of valid values for the specified temporal field.
   * <p>
   * The range of {@link IsoFields#WEEK_OF_WEEK_BASED_YEAR} is refined to the 52 or 53 weeks of
   * this week-based year. The IsoFields cannot refine their ranges themselves, as they require a
   * full date. The range of {@link #WEEK_OF_YEAR} is refined to the weeks of the calendar year
   * of the date, as the field requires the day of the year, which week dates do not support.
   *
   * @param field the temporal field to query
   * @return the range of valid values for the field
   * @throws UnsupportedTemporalTypeException if the field is not supported by
   * this week date
   * @throws java.time.DateTimeException if the week-based year is out of range, or if
   * {@link #WEEK_OF_YEAR} is requested and the week is out of range for the week-based year
   */
  @Override
  default ValueRange range(TemporalField field) {
    if (field == IsoFields.WEEK_BASED_YEAR) {
      return field.range();
    } else if (field == IsoFields.WEEK_OF_WEEK_BASED_YEAR) {
      // 4 January always lies in week 1
      return LocalDate.of(getWeekBasedYear(), 1, 4).range(field);
    } else if (field == WEEK_OF_YEAR && isPresentDayOfWeek()) {
      return toLocalDate().range(field);
    }
    return ASTWeekDateTOP.super.range(field);
  }
}
