/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals.util.ISOTemporalsConversions;
import de.monticore.temporal.timetriggerconditions._ast.ASTEveryTimeCondition;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Ensures that every-time intervals and repetition counts are valid.
 * <p>
 * Requires an initialized {@link TypeCheck3}.
 */
public class EveryTimeConditionHasValidRepetitions implements
    TimeTriggerConditionsASTEveryTimeConditionCoCo {

  public static final String INVALID_INTERVAL = "0xF0009";
  public static final String INVALID_START = "0xF000A";
  public static final String NON_POSITIVE_REPETITIONS = "0xF000B";

  @Override
  public void check(ASTEveryTimeCondition node) {
    validateInterval(node.getPeriod(), node);
    if (node.isPresentStart()) {
      validateStart(node.getStart(), node);
    }
    if (node.isPresentTimes()) {
      validateRepetitions(node.getTimes(), node);
    }
  }

  protected void validateInterval(ASTSIUnitLiteral duration, ASTEveryTimeCondition node) {
    SymTypeExpression type = TypeCheck3.typeOf(duration);
    if (!type.isObscureType() && !isTimeDuration(type)) {
      report(node, INVALID_INTERVAL, "Every timer interval unit must be a unit of time,"
          + " e.g., ms, s, min, h, or d, but got " + type.printFullName());
    }
  }

  protected void validateStart(ASTISODateTime start, ASTEveryTimeCondition node) {
    try {
      ISOTemporalsConversions.toOffsetDateTime(start);
    } catch (java.time.format.DateTimeParseException exception) {
      report(node, INVALID_START,
          "Every timer start must be a valid ISO date-time with an explicit UTC offset");
    }
  }

  protected void validateRepetitions(ASTNatLiteral literal, ASTEveryTimeCondition node) {
    if (literal.getDigits().chars().allMatch(c -> c == '0')) {
      report(node, NON_POSITIVE_REPETITIONS, "Every timer repetition count must be greater than 0");
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

  protected void report(ASTEveryTimeCondition node, String code, String requirement) {
    Log.error(code + " " + requirement + ".",
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }
}
