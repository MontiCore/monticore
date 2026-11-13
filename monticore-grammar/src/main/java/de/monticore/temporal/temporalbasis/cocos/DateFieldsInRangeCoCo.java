/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.cocos;

import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._cocos.TemporalBasisASTDateCoCo;
import de.se_rwth.commons.logging.Log;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ValueRange;

import static java.time.temporal.ChronoField.*;

/**
 * Checks that the month, the day of the month and the day of the year of a date exist,
 * e.g. rejects 2017-13-04, 2017-02-29 and 2017-366. The ranges are taken from java.time.
 * The fields of ISO week dates are checked by {@code WeekDateFieldsInRangeCoCo} of ISOTemporals.
 */
public class DateFieldsInRangeCoCo implements TemporalBasisASTDateCoCo {

  public static final String MONTH_ERROR_CODE = "0x5C964";

  public static final String DAY_OF_MONTH_ERROR_CODE = "0xB5069";

  public static final String DAY_OF_YEAR_ERROR_CODE = "0x3C91B";

  public static final String MONTH_ERROR_MESSAGE = "%s: The month %d is not in the range 1 to 12";

  public static final String DAY_OF_MONTH_ERROR_MESSAGE = "%s: The day %d is not in the range 1 to %d";

  public static final String DAY_OF_YEAR_ERROR_MESSAGE = "%s: The day of the year %d is not in the range 1 to %d";

  @Override
  public void check(ASTDate node) {
    if (node.isSupported(MONTH_OF_YEAR) && !MONTH_OF_YEAR.range().isValidValue(node.getLong(MONTH_OF_YEAR))) {
      Log.error(String.format(MONTH_ERROR_MESSAGE, MONTH_ERROR_CODE, node.getLong(MONTH_OF_YEAR)),
          node.get_SourcePositionStart());
    }

    if (node.isSupported(DAY_OF_MONTH)) {
      long day = node.getLong(DAY_OF_MONTH);
      ValueRange range = dayOfMonthRange(node);
      if (!range.isValidValue(day)) {
        Log.error(String.format(DAY_OF_MONTH_ERROR_MESSAGE, DAY_OF_MONTH_ERROR_CODE, day, range.getMaximum()),
            node.get_SourcePositionStart());
      }
    }

    if (node.isSupported(DAY_OF_YEAR)) {
      long day = node.getLong(DAY_OF_YEAR);
      ValueRange range = hasValidYear(node)
          ? LocalDate.of((int) node.getLong(YEAR), 1, 1).range(DAY_OF_YEAR)
          : DAY_OF_YEAR.range();
      if (!range.isValidValue(day)) {
        Log.error(String.format(DAY_OF_YEAR_ERROR_MESSAGE, DAY_OF_YEAR_ERROR_CODE, day, range.getMaximum()),
            node.get_SourcePositionStart());
      }
    }
  }

  /**
   * @return the days of the month of the date, of the longest month if the month is unknown
   * or invalid, and of the month in a leap year if the year is unknown
   */
  protected ValueRange dayOfMonthRange(ASTDate node) {
    if (!node.isSupported(MONTH_OF_YEAR) || !MONTH_OF_YEAR.range().isValidValue(node.getLong(MONTH_OF_YEAR))) {
      return DAY_OF_MONTH.range();
    }
    Month month = Month.of((int) node.getLong(MONTH_OF_YEAR));
    return hasValidYear(node)
        ? LocalDate.of((int) node.getLong(YEAR), month, 1).range(DAY_OF_MONTH)
        : ValueRange.of(1, month.maxLength());
  }

  protected boolean hasValidYear(ASTDate node) {
    return node.isSupported(YEAR) && YEAR.range().isValidValue(node.getLong(YEAR));
  }
}
