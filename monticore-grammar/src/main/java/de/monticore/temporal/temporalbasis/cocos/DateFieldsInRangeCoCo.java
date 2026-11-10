/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.cocos;

import de.monticore.temporal.temporalbasis._ast.ASTDate;
import de.monticore.temporal.temporalbasis._cocos.TemporalBasisASTDateCoCo;
import de.se_rwth.commons.logging.Log;

import java.time.Month;
import java.time.Year;

import static java.time.temporal.ChronoField.*;

/**
 * Checks that the month, the day of the month and the day of the year of a date exist,
 * e.g. rejects 2017-13-04, 2017-02-29 and 2017-366. Week dates are not checked here.
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
    boolean validMonth = true;
    if (node.isSupported(MONTH_OF_YEAR)) {
      long month = node.getLong(MONTH_OF_YEAR);
      validMonth = MONTH_OF_YEAR.range().isValidValue(month);
      if (!validMonth) {
        Log.error(String.format(MONTH_ERROR_MESSAGE, MONTH_ERROR_CODE, month),
            node.get_SourcePositionStart());
      }
    }

    if (node.isSupported(DAY_OF_MONTH)) {
      long day = node.getLong(DAY_OF_MONTH);
      int maxDay = 31;
      if (validMonth && node.isSupported(MONTH_OF_YEAR)) {
        Month month = Month.of((int) node.getLong(MONTH_OF_YEAR));
        maxDay = node.isSupported(YEAR)
            ? month.length(Year.isLeap(node.getLong(YEAR)))
            : month.maxLength();
      }
      if (day < 1 || day > maxDay) {
        Log.error(String.format(DAY_OF_MONTH_ERROR_MESSAGE, DAY_OF_MONTH_ERROR_CODE, day, maxDay),
            node.get_SourcePositionStart());
      }
    }

    if (node.isSupported(DAY_OF_YEAR)) {
      long day = node.getLong(DAY_OF_YEAR);
      int maxDay = !node.isSupported(YEAR) || Year.isLeap(node.getLong(YEAR)) ? 366 : 365;
      if (day < 1 || day > maxDay) {
        Log.error(String.format(DAY_OF_YEAR_ERROR_MESSAGE, DAY_OF_YEAR_ERROR_CODE, day, maxDay),
            node.get_SourcePositionStart());
      }
    }
  }
}
