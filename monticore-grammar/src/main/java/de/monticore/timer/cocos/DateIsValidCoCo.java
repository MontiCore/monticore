/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.timer._ast.ASTDate;
import de.monticore.timer._cocos.TimerASTDateCoCo;
import de.se_rwth.commons.logging.Log;

public class DateIsValidCoCo implements TimerASTDateCoCo {

  public static final String ERROR_CODE = "0xA0916";

  public static final String ERROR_MSG_MONTH_FORMAT = " Not a valid month format.";

  public static final String ERROR_MSG_DAY_FORMAT = " Not a valid day format for month %d.";

  @Override
  public void check(final ASTDate date) {
    if (date.getMonth().getValue() < 1 || date.getMonth().getValue() > 12) {
      Log.error(ERROR_CODE + ERROR_MSG_MONTH_FORMAT, date.getMonth().get_SourcePositionStart(), date.getMonth().get_SourcePositionEnd());
    } else if (date.getDay().getValue() < 1 || date.getDay().getValue() > getMaxDayForMonth(date.getYear().getValue(), date.getMonth().getValue())) {
      Log.error(ERROR_CODE + String.format(ERROR_MSG_DAY_FORMAT, date.getMonth().getValue()), date.getDay().get_SourcePositionStart(), date.getDay().get_SourcePositionEnd());
    }
  }

  protected int getMaxDayForMonth(int year, int month) {
    switch (month) {
      case 1:
        return 31;
      case 2:
        return year % 4 == 0 ? 29 : 28;
      case 3:
        return 31;
      case 4:
        return 30;
      case 5:
        return 31;
      case 6:
        return 30;
      case 7:
        return 31;
      case 8:
        return 31;
      case 9:
        return 30;
      case 10:
        return 31;
      case 11:
        return 30;
      case 12:
        return 31;
      default:
        return -1;
    }
  }
}
