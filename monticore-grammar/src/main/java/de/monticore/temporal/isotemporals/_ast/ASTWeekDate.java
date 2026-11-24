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
 * See {@code Temporal.md} for the week-based year and the supported fields.
 */
public interface ASTWeekDate extends ASTWeekDateTOP {

  /** Week of the calendar year, not the ISO week ({@link IsoFields#WEEK_OF_WEEK_BASED_YEAR}). */
  TemporalField WEEK_OF_YEAR = WeekFields.ISO.weekOfYear();

  int getWeekBasedYear();

  int getWeekOfWeekBasedYear();

  boolean isPresentDayOfWeek();

  int getDayOfWeek();

  default DayOfWeek toDayOfWeek() {
    return DayOfWeek.of(getDayOfWeek());
  }

  /** Calendar year of the date, which differs from the week-based year around the turn of the year. */
  default int getCalendarYear() {
    return toLocalDate().getYear();
  }

  @Override
  default boolean isSupported(TemporalField field) {
    // The calendar year and its weeks can only be derived from a full date
    if (field == ChronoField.DAY_OF_WEEK || field == ChronoField.YEAR || field == WEEK_OF_YEAR) {
      return isPresentDayOfWeek();
    }
    return field == IsoFields.WEEK_BASED_YEAR || field == IsoFields.WEEK_OF_WEEK_BASED_YEAR;
  }

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
   * Refines the range of the ISO week to the 52 or 53 weeks of the week-based year,
   * so that {@code get()} rejects, e.g., week 53 of 2021.
   */
  @Override
  default ValueRange range(TemporalField field) {
    if (field == IsoFields.WEEK_BASED_YEAR) {
      return field.range();
    } else if (field == IsoFields.WEEK_OF_WEEK_BASED_YEAR) {
      // ISO 8601 defines week 1 as the week with January 4. January 1 may still lie in the last
      // week of the previous week-based year, whose number of weeks can differ.
      return LocalDate.of(getWeekBasedYear(), 1, 4).range(field);
    } else if (field == WEEK_OF_YEAR && isPresentDayOfWeek()) {
      // The default range requires the day of the year
      return toLocalDate().range(field);
    }
    return ASTWeekDateTOP.super.range(field);
  }
}
