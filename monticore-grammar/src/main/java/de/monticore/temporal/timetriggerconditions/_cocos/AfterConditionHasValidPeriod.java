/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.temporal.isotemporals._ast.ASTFullPeriod;
import de.monticore.temporal.isotemporals._ast.ASTISOPeriod;
import de.monticore.temporal.isotemporals._ast.ASTWeekPeriod;
import de.monticore.temporal.timetriggerconditions._ast.ASTAfterCondition;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an ISO 8601 after timer represents a positive period.
 */
public class AfterConditionHasValidPeriod implements
    TimeTriggerConditionsASTAfterConditionCoCo {

  /** Error code for an ISO period that does not denote a positive duration. */
  public static final String NON_POSITIVE_PERIOD = "0xF0008";

  /** Creates the stateless ISO-period CoCo. */
  public AfterConditionHasValidPeriod() {
  }

  @Override
  public void check(ASTAfterCondition node) {
    if (!node.isPresentPeriod()) {
      return;
    }
    if (!isPositive(node.getPeriod())) {
      Log.error(NON_POSITIVE_PERIOD + " ISO after timer period must be greater than 0.", node
          .get_SourcePositionStart());
    }
  }

  /** Returns whether at least one period component is positive. */
  protected boolean isPositive(ASTISOPeriod period) {
    if (period instanceof ASTWeekPeriod weekPeriod) {
      return weekPeriod.getWeeks() > 0;
    }
    if (period instanceof ASTFullPeriod fullPeriod) {
      return (fullPeriod.isPresentYears() && fullPeriod.getYears() > 0) || (fullPeriod
          .isPresentMonths() && fullPeriod.getMonths() > 0) || (fullPeriod.isPresentDays()
              && fullPeriod.getDays() > 0) || (fullPeriod.isPresentHours() && fullPeriod.getHours()
                  > 0) || (fullPeriod.isPresentMinutes() && fullPeriod.getMinutes() > 0)
          || (fullPeriod.isPresentSeconds() && fullPeriod.getSeconds() > 0) || hasPositiveFraction(
              fullPeriod);
    }
    return false;
  }

  /** Returns whether a parsed fractional component contains a non-zero digit. */
  protected boolean hasPositiveFraction(ASTFullPeriod period) {
    return period.isPresentDecimalDigits() && period.getDecimalDigits().chars().anyMatch(
        character -> character != '0');
  }

}
