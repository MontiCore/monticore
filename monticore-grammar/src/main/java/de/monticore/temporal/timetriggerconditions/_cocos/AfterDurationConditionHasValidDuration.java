/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.se_rwth.commons.logging.Log;
import de.monticore.temporal.timetriggerconditions._ast.ASTAfterDurationCondition;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static de.monticore.siunit.util.SIUnitLiteralSupport.ALLOWED_TIME_UNITS;
import static de.monticore.siunit.util.SIUnitLiteralSupport.numericValue;
import static de.monticore.siunit.util.SIUnitLiteralSupport.simpleUnit;

/**
 * Checks that an after timer uses a positive duration and a time unit.
 */
public class AfterDurationConditionHasValidDuration implements
    TimeTriggerConditionsASTAfterDurationConditionCoCo {
  
  /** Error code for a zero or otherwise non-positive duration. */
  public static final String NON_POSITIVE_DURATION = "0xF0001";
  /** Error code for a non-time, compound, or unsupported unit. */
  public static final String UNSUPPORTED_UNIT = "0xF0002";
  /** Error code for a numeric literal that cannot be represented. */
  public static final String INVALID_NUMERIC_LITERAL = "0xF0003";
  /** The complete set of simple SI-unit spellings supported by {@code after}. */
  public static final Set<String> ALLOWED_UNITS = ALLOWED_TIME_UNITS;
  
  /** Creates the stateless duration CoCo. */
  public AfterDurationConditionHasValidDuration() {
  }
  
  @Override
  public void check(ASTAfterDurationCondition node) {
    ASTSIUnitLiteral duration = node.getDuration();
    Optional<BigDecimal> value = numericValue(duration.getNumericLiteral());
    Optional<String> unit = simpleUnit(duration.getSIUnit());
    
    if (value.isEmpty()) {
      Log.error(INVALID_NUMERIC_LITERAL + " Invalid numeric value in after timer.", node
          .get_SourcePositionStart());
    }
    else if (value.get().signum() <= 0) {
      Log.error(NON_POSITIVE_DURATION + " After timer duration must be greater than 0.", node
          .get_SourcePositionStart());
    }
    if (unit.isEmpty() || !ALLOWED_UNITS.contains(unit.get())) {
      Log.error(UNSUPPORTED_UNIT + " After timer unit must be one of ms, s, min, h, or d.", node
          .get_SourcePositionStart());
    }
  }
  
}
