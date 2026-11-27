/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.cocos;

import de.monticore.temporal.isotemporals._ast.ASTWeekDate;
import de.monticore.temporal.isotemporals._cocos.ISOTemporalsASTWeekDateCoCo;
import de.se_rwth.commons.logging.Log;

import java.time.LocalDate;
import java.time.temporal.ValueRange;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR;

/**
 * Checks that the week and the day of the week of a week date exist, e.g. rejects 2017-W54,
 * 2017-W53 (2017 has 52 weeks, ISO 8601-1 4.3.4) and 2017-W01-8.
 */
public class WeekDateFieldsInRangeCoCo implements ISOTemporalsASTWeekDateCoCo {

  public static final String WEEK_ERROR_CODE = "0x38138";

  public static final String DAY_OF_WEEK_ERROR_CODE = "0x85517";

  public static final String WEEK_ERROR_MESSAGE = "%s: The week %d is not in the range 1 to %d";

  public static final String DAY_OF_WEEK_ERROR_MESSAGE = "%s: The day of the week %d is not in the range 1 to 7";

  @Override
  public void check(ASTWeekDate node) {
    ValueRange weeks = weekRange(node.getYear());
    if (!weeks.isValidValue(node.getWeek())) {
      Log.error(String.format(WEEK_ERROR_MESSAGE, WEEK_ERROR_CODE, node.getWeek(), weeks.getMaximum()),
          node.get_SourcePositionStart());
    }
    if (node.isPresentDayOfWeek() && !DAY_OF_WEEK.range().isValidValue(node.getDayOfWeek())) {
      Log.error(String.format(DAY_OF_WEEK_ERROR_MESSAGE, DAY_OF_WEEK_ERROR_CODE, node.getDayOfWeek()),
          node.get_SourcePositionStart());
    }
  }

  /**
   * @return the 52 or 53 weeks of the week-based year, or 1 to 53 if java.time cannot represent the year
   */
  protected ValueRange weekRange(int weekBasedYear) {
    if (!YEAR.range().isValidValue(weekBasedYear)) {
      return WEEK_OF_WEEK_BASED_YEAR.range();
    }
    // 4 January always lies in week 1 of its week-based year
    return LocalDate.of(weekBasedYear, 1, 4).range(WEEK_OF_WEEK_BASED_YEAR);
  }
}
