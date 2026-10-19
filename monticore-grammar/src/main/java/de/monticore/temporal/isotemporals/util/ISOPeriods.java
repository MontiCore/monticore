/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.util;

import de.monticore.temporal.isotemporals._ast.ASTFullPeriod;
import de.monticore.temporal.isotemporals._ast.ASTISOPeriod;
import de.monticore.temporal.isotemporals._ast.ASTWeekPeriod;

import java.util.Objects;

/**
 * Static semantic classification of {@link ASTISOPeriod} nodes.
 *
 * <p>The classification mirrors the split between {@link java.time.Duration} and
 * {@link java.time.Period}: hours, minutes, and seconds have a fixed length, whereas
 * years, months, weeks, and days are calendar units whose length depends on the
 * start date and time zone (e.g. a day spanning a daylight saving change has
 * 23 or 25 hours).</p>
 */
public final class ISOPeriods {

  /** The two kinds of ISO periods. */
  public enum PeriodKind {
    /** A period consisting only of hours, minutes, and seconds. */
    FIXED,
    /** A period containing a non-zero year, month, week, or day component. */
    CALENDAR_RELATIVE
  }

  private ISOPeriods() {
  }

  /**
   * Classifies a structured ISO period without choosing a runtime calendar policy.
   *
   * @param period the parsed ISO period
   * @return {@link PeriodKind#CALENDAR_RELATIVE} if the period contains a non-zero
   * year, month, week, or day component, otherwise {@link PeriodKind#FIXED}
   * @throws NullPointerException if {@code period} is {@code null}
   * @throws IllegalArgumentException if an unknown period implementation is supplied
   */
  public static PeriodKind classify(ASTISOPeriod period) {
    Objects.requireNonNull(period, "period");
    if (period instanceof ASTWeekPeriod weekPeriod) {
      return weekPeriod.getWeeks() > 0 ? PeriodKind.CALENDAR_RELATIVE : PeriodKind.FIXED;
    }
    if (period instanceof ASTFullPeriod fullPeriod) {
      if ((fullPeriod.isPresentYears() && fullPeriod.getYears() > 0) || (fullPeriod
          .isPresentMonths() && fullPeriod.getMonths() > 0) || (fullPeriod.isPresentDays()
          && fullPeriod.getDays() > 0)) {
        return PeriodKind.CALENDAR_RELATIVE;
      }
      return PeriodKind.FIXED;
    }
    throw new IllegalArgumentException("Unsupported ISO period AST type: " + period.getClass()
        .getName());
  }

  /**
   * Determines whether a period requires a calendar, time zone, and end-of-month policy
   * to be resolved to an exact length.
   *
   * @param period the parsed ISO period
   * @return whether the period is calendar-relative
   */
  public static boolean isCalendarRelative(ASTISOPeriod period) {
    return classify(period) == PeriodKind.CALENDAR_RELATIVE;
  }

}
