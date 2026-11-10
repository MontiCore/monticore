/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.cocos;

import de.monticore.temporal.temporalbasis._ast.ASTTime;
import de.monticore.temporal.temporalbasis._cocos.TemporalBasisASTTimeCoCo;
import de.se_rwth.commons.logging.Log;

import java.time.temporal.ChronoField;

import static java.time.temporal.ChronoField.*;

/**
 * Checks that the hour, minute and second of a time exist, e.g. rejects 25:00 and 12:60.
 * The end of the day (hour 24) and leap seconds (second 60) are accepted, since their
 * semantics is not decided yet.
 */
public class TimeFieldsInRangeCoCo implements TemporalBasisASTTimeCoCo {

  public static final String HOUR_ERROR_CODE = "0xA0F95";

  public static final String MINUTE_ERROR_CODE = "0xE6C6C";

  public static final String SECOND_ERROR_CODE = "0xC7C1C";

  public static final String ERROR_MESSAGE = "%s: The %s %d is not in the range 0 to %d";

  @Override
  public void check(ASTTime node) {
    check(node, HOUR_OF_DAY, 24, HOUR_ERROR_CODE, "hour");
    check(node, MINUTE_OF_HOUR, 59, MINUTE_ERROR_CODE, "minute");
    check(node, SECOND_OF_MINUTE, 60, SECOND_ERROR_CODE, "second");
  }

  protected void check(ASTTime node, ChronoField field, int max, String errorCode, String name) {
    if (node.isSupported(field)) {
      long value = node.getLong(field);
      if (value < 0 || value > max) {
        Log.error(String.format(ERROR_MESSAGE, errorCode, name, value, max),
            node.get_SourcePositionStart());
      }
    }
  }
}
