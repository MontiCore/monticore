/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.cocos;

import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.monticore.temporal.isotemporals._cocos.ISOTemporalsASTISOTimeCoCo;
import de.monticore.temporal.temporalbasis.cocos.TimeFieldsInRangeCoCo;
import de.se_rwth.commons.logging.Log;

import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

/**
 * Checks the parts of an ISO time that {@link TimeFieldsInRangeCoCo} cannot access as fields:
 * <ul>
 *   <li>The minutes of the time shift are in the range 0 to 59, e.g. rejects 12:00+05:75.</li>
 *   <li>The end of the day has no fraction of the hour or minute, e.g. rejects T24,5 and 24:00,5.
 *   A fraction of the second is checked by {@link TimeFieldsInRangeCoCo}.</li>
 * </ul>
 */
public class ISOTimeFieldsInRangeCoCo implements ISOTemporalsASTISOTimeCoCo {

  public static final String TIME_SHIFT_MINUTE_ERROR_CODE = "0x686C7";

  public static final String TIME_SHIFT_MINUTE_ERROR_MESSAGE =
      "%s: The minute %d of the time shift is not in the range 0 to 59";

  @Override
  public void check(ASTISOTime node) {
    if (node.isPresentTimeShiftMinute()
        && !MINUTE_OF_HOUR.range().isValidValue(Math.abs(node.getTimeShiftMinute()))) {
      Log.error(String.format(TIME_SHIFT_MINUTE_ERROR_MESSAGE, TIME_SHIFT_MINUTE_ERROR_CODE,
              Math.abs(node.getTimeShiftMinute())),
          node.get_SourcePositionStart());
    }

    // A nonzero minute or second at the end of the day is already reported by TimeFieldsInRangeCoCo
    if (node.getHour() == TimeFieldsInRangeCoCo.END_OF_DAY_HOUR
        && !node.isPresentSecond()
        && (!node.isPresentMinute() || node.getMinute() == 0)
        && node.isPresentDecimalDigits()
        && !node.getDecimalDigits().matches("0*")) {
      Log.error(String.format(TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_MESSAGE,
              TimeFieldsInRangeCoCo.END_OF_DAY_ERROR_CODE),
          node.get_SourcePositionStart());
    }
  }
}
