/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.timer._ast.ASTTime;
import de.monticore.timer._cocos.TimerASTTimeCoCo;
import de.se_rwth.commons.logging.Log;

public class TimeIsValidCoCo implements TimerASTTimeCoCo {

  public static final String ERROR_CODE = "0xA0915";

  public static final String ERROR_MSG_FORMAT = " Not a valid %s format.";

  @Override
  public void check(final ASTTime time) {
    if (time.getHours().getValue() > 23) {
      Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, "hour"), time.getHours().get_SourcePositionStart(), time.getHours().get_SourcePositionEnd());
    }

    if (time.getMinutes().getValue() > 59) {
      Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, "minute"), time.getMinutes().get_SourcePositionStart(), time.getMinutes().get_SourcePositionEnd());
    }

    if (time.isPresentSeconds() && time.getSeconds().getValue() > 59) {
      Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, "second"), time.getSeconds().get_SourcePositionStart(), time.getSeconds().get_SourcePositionEnd());
    }
  }
}
