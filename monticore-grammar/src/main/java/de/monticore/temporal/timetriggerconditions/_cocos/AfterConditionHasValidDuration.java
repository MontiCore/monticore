/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.temporal.timetriggerconditions._ast.ASTAfterCondition;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that an after timer uses a time unit.
 * <p>
 * Requires an initialized {@link TypeCheck3}.
 */
public class AfterConditionHasValidDuration implements
    TimeTriggerConditionsASTAfterConditionCoCo {

  /** Error code for a unit that is not of dimension time. */
  public static final String UNSUPPORTED_UNIT = "0xF0002";

  @Override
  public void check(ASTAfterCondition node) {
    if (!node.isPresentDuration()) {
      return;
    }
    ASTSIUnitLiteral duration = node.getDuration();
    SymTypeExpression type = TypeCheck3.typeOf(duration);
    if (!type.isObscureType() && !isTimeDuration(type)) {
      Log.error(UNSUPPORTED_UNIT + " After timer unit must be a unit of time, e.g., ms, s, min, h,"
          + " or d, but got " + type.printFullName() + ".", node.get_SourcePositionStart());
    }
  }

  /**
   * @return whether the type is an SI unit (with or without a numeric)
   * of dimension time, e.g., {@code [ms]<int>}, {@code [h]<double>}, or {@code [s]}
   */
  protected boolean isTimeDuration(SymTypeExpression type) {
    SymTypeOfSIUnit siUnit;
    if (type.isNumericWithSIUnitType()) {
      siUnit = type.asNumericWithSIUnitType().getSIUnitType();
    }
    else if (type.isSIUnitType()) {
      siUnit = type.asSIUnitType();
    }
    else {
      return false;
    }
    SymTypeOfSIUnit second = SymTypeExpressionFactory.createSIUnit(
        List.of(SymTypeExpressionFactory.createSIUnitBasic("s")), List.of());
    return SIUnitTypeRelations.isOfSameDimension(siUnit, second);
  }

}
