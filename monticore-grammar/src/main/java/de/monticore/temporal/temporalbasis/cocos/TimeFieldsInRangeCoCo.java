/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.cocos;

import de.monticore.temporal.temporalbasis._ast.ASTTime;
import de.monticore.temporal.temporalbasis._cocos.TemporalBasisASTTimeCoCo;
import de.se_rwth.commons.logging.Log;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import static java.time.temporal.ChronoField.*;

/**
 * Checks that the hour, minute, second and time shift of a time exist, e.g. rejects 25:00,
 * 12:60 and 12:00+19:00. The ranges are taken from java.time, with two exceptions of
 * ISO 8601-1:2019/Amd.1:2022:
 * <ul>
 *   <li>Hour 24 denotes the end of the day and is only allowed as 24:00 or 24:00:00.</li>
 *   <li>Second 60 denotes a positive leap second and is only allowed as the last second of a
 *   UTC minute, i.e. 23:59:60Z. With a time shift, the time is converted to UTC first. Without a
 *   time shift, the offset to UTC is unknown, so a leap second is allowed in any minute.</li>
 * </ul>
 * Fractions of the hour or minute are checked by {@code ISOTimeFieldsInRangeCoCo} of ISOTemporals.
 */
public class TimeFieldsInRangeCoCo implements TemporalBasisASTTimeCoCo {

  public static final String HOUR_ERROR_CODE = "0xA0F95";

  public static final String MINUTE_ERROR_CODE = "0xE6C6C";

  public static final String SECOND_ERROR_CODE = "0xC7C1C";

  public static final String END_OF_DAY_ERROR_CODE = "0x81C59";

  public static final String LEAP_SECOND_ERROR_CODE = "0xF2450";

  public static final String TIME_SHIFT_ERROR_CODE = "0x54935";

  public static final String ERROR_MESSAGE = "%s: The %s %d is not in the range 0 to %d";

  public static final String END_OF_DAY_ERROR_MESSAGE =
      "%s: The hour 24 denotes the end of the day and requires minute, second and fraction to be 0";

  public static final String LEAP_SECOND_ERROR_MESSAGE =
      "%s: The second 60 is a leap second and only allowed at 23:59:60 UTC, but the time is %s UTC";

  public static final String TIME_SHIFT_ERROR_MESSAGE =
      "%s: The time shift of %d seconds is not in the range %d to %d";

  /** The hour of the end of the day, e.g. 24:00 */
  public static final int END_OF_DAY_HOUR = 24;

  /** The second of a positive leap second, e.g. 23:59:60Z */
  public static final int LEAP_SECOND = 60;

  /** A leap second is inserted after 23:59:59 UTC */
  protected static final LocalTime LEAP_SECOND_MINUTE = LocalTime.of(23, 59);

  @Override
  public void check(ASTTime node) {
    check(node, HOUR_OF_DAY, END_OF_DAY_HOUR, HOUR_ERROR_CODE, "hour");
    check(node, MINUTE_OF_HOUR, MINUTE_OF_HOUR.range().getMaximum(), MINUTE_ERROR_CODE, "minute");
    check(node, SECOND_OF_MINUTE, LEAP_SECOND, SECOND_ERROR_CODE, "second");

    if (node.isSupported(OFFSET_SECONDS) && !isSupportedInRange(node, OFFSET_SECONDS)) {
      Log.error(String.format(TIME_SHIFT_ERROR_MESSAGE, TIME_SHIFT_ERROR_CODE,
              node.getLong(OFFSET_SECONDS), OFFSET_SECONDS.range().getMinimum(),
              OFFSET_SECONDS.range().getMaximum()),
          node.get_SourcePositionStart());
    }

    if (isSupportedWithValue(node, HOUR_OF_DAY, END_OF_DAY_HOUR)) {
      checkEndOfDay(node);
    } else if (isSupportedWithValue(node, SECOND_OF_MINUTE, LEAP_SECOND)) {
      checkLeapSecond(node);
    }
  }

  protected void check(ASTTime node, ChronoField field, long max, String errorCode, String name) {
    if (node.isSupported(field)) {
      long value = node.getLong(field);
      if (value < 0 || value > max) {
        Log.error(String.format(ERROR_MESSAGE, errorCode, name, value, max),
            node.get_SourcePositionStart());
      }
    }
  }

  protected void checkEndOfDay(ASTTime node) {
    if (isSupportedWithNonzeroValue(node, MINUTE_OF_HOUR)
        || isSupportedWithNonzeroValue(node, SECOND_OF_MINUTE)
        || isSupportedWithNonzeroValue(node, NANO_OF_SECOND)) {
      Log.error(String.format(END_OF_DAY_ERROR_MESSAGE, END_OF_DAY_ERROR_CODE),
          node.get_SourcePositionStart());
    }
  }

  protected void checkLeapSecond(ASTTime node) {
    // Without a valid time shift the time cannot be converted to UTC; invalid fields are reported above
    if (!isSupportedInRange(node, OFFSET_SECONDS)
        || !isSupportedInRange(node, HOUR_OF_DAY)
        || !isSupportedInRange(node, MINUTE_OF_HOUR)) {
      return;
    }
    LocalTime utc = LocalTime.of((int) node.getLong(HOUR_OF_DAY), (int) node.getLong(MINUTE_OF_HOUR))
        .minusSeconds(node.getLong(OFFSET_SECONDS));
    if (!utc.equals(LEAP_SECOND_MINUTE)) {
      Log.error(String.format(LEAP_SECOND_ERROR_MESSAGE, LEAP_SECOND_ERROR_CODE, utc),
          node.get_SourcePositionStart());
    }
  }

  protected boolean isSupportedWithValue(ASTTime node, ChronoField field, long value) {
    return node.isSupported(field) && node.getLong(field) == value;
  }

  protected boolean isSupportedInRange(ASTTime node, ChronoField field) {
    return node.isSupported(field) && field.range().isValidValue(node.getLong(field));
  }

  protected boolean isSupportedWithNonzeroValue(ASTTime node, ChronoField field) {
    return node.isSupported(field) && node.getLong(field) != 0;
  }
}
