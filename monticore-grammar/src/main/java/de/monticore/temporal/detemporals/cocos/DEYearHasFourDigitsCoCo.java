/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals.cocos;

import de.monticore.temporal.detemporals._ast.ASTDEDate;
import de.monticore.temporal.detemporals._cocos.DETemporalsASTDEDateCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the year of a German date has four digits, as required by DIN 5008:2020 11.4,
 * e.g. rejects 04.12.17 and 4. Dez. 17. A shorter year would otherwise be taken literally,
 * e.g. 17 as the year 17 instead of 2017.
 */
public class DEYearHasFourDigitsCoCo implements DETemporalsASTDEDateCoCo {

  public static final String ERROR_CODE = "0x605D1";

  public static final String ERROR_MESSAGE = "%s: The year %s must have four digits";

  @Override
  public void check(ASTDEDate node) {
    if (node.getYearSource().length() != 4) {
      Log.error(String.format(ERROR_MESSAGE, ERROR_CODE, node.getYearSource()),
          node.get_SourcePositionStart());
    }
  }
}
