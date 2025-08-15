/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.timer._ast.ASTPeriod;
import de.monticore.timer._cocos.TimerASTPeriodCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

public class PeriodIsValidCoCo implements TimerASTPeriodCoCo {

  public static final String ERROR_CODE = "0xA0913";

  public static final String ERROR_MSG_FORMAT = " time periods need to be of type time.";

  public static final String ERROR_CODE_EMPTY = "0xA0914";

  public static final String ERROR_MSG_EMPTY_FORMAT = " time periods cannot be empty.";

  @Override
  public void check(ASTPeriod node) {
    if (node.isPresentSIUnitLiteral()) {
      check(node.getSIUnitLiteral());
    } else {
      checkIso(node);
    }
  }

  protected void check(ASTSIUnitLiteral node) {
    // Check that SIUnit Literal is of type time
    SymTypeExpression periodType = TypeCheck3.typeOf(node);
    if (!(periodType.isSIUnitType() || periodType.isNumericWithSIUnitType())) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return;
    }

    SymTypeOfSIUnit periodSiType = SIUnitTypeRelations.internal_normalize(periodType.isNumericWithSIUnitType()
        ? periodType.asNumericWithSIUnitType().getSIUnitType()
        : periodType.asSIUnitType());
    if (!periodSiType.getDenominator().isEmpty()
        || periodSiType.getNumerator().size() != 1
        || !periodSiType.getNumerator().get(0).getDimension().equals("s")) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }


  protected void checkIso(ASTPeriod node) {
    // Only restriction of ISO_8601 is that it cannot be empty
    if (!(node.isPresentYear()
        || node.isPresentMonth()
        || node.isPresentWeek()
        || node.isPresentDay()
        || node.isPresentHour()
        || node.isPresentMinute()
        || node.isPresentSecond()
    )) {
      Log.error(ERROR_CODE_EMPTY + ERROR_MSG_EMPTY_FORMAT, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
