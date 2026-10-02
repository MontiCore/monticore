/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions.semantics;

import de.monticore.temporal.isotemporals._ast.ASTFullPeriod;
import de.monticore.temporal.isotemporals._ast.ASTISOPeriod;
import de.monticore.temporal.isotemporals._ast.ASTWeekPeriod;

import java.util.Objects;

/** Static semantic classification for ISO periods used by {@code after}. */
public final class AfterPeriodSemantics {
  
  /** The two duration classes exposed to TimeTrigger consumers. */
  public enum DurationKind {
    /** A duration consisting only of weeks, days, hours, minutes, and seconds. */
    FIXED,
    /** A duration containing a year or month component. */
    CALENDAR_RELATIVE
  }
  
  private AfterPeriodSemantics() {
  }
  
  /**
   * Classifies a structured ISO period without choosing a runtime calendar policy.
   *
   * @param period the parsed ISO period
   * @return {@link DurationKind#CALENDAR_RELATIVE} if the period contains a year or
   * month component, otherwise {@link DurationKind#FIXED}
   * @throws NullPointerException if {@code period} is {@code null}
   * @throws IllegalArgumentException if an unknown period implementation is supplied
   */
  public static DurationKind classify(ASTISOPeriod period) {
    Objects.requireNonNull(period, "period");
    if (period instanceof ASTWeekPeriod) {
      return DurationKind.FIXED;
    }
    if (period instanceof ASTFullPeriod fullPeriod) {
      if ((fullPeriod.isPresentYears() && fullPeriod.getYears() > 0) || (fullPeriod
          .isPresentMonths() && fullPeriod.getMonths() > 0)) {
        return DurationKind.CALENDAR_RELATIVE;
      }
      return DurationKind.FIXED;
    }
    throw new IllegalArgumentException("Unsupported ISO period AST type: " + period.getClass()
        .getName());
  }
  
  /**
   * Determines whether a period requires a calendar, timezone, and end-of-month policy.
   *
   * @param period the parsed ISO period
   * @return whether the period is calendar-relative
   */
  public static boolean isCalendarRelative(ASTISOPeriod period) {
    return classify(period) == DurationKind.CALENDAR_RELATIVE;
  }
  
}
