/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.se_rwth.commons.logging.Log;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import de.monticore.temporal.timetriggerconditions._ast.ASTEveryTimeCondition;
import de.monticore.temporal.isotemporals.util.ISOTemporalsConversions;

import static de.monticore.siunit.util.SIUnitLiteralSupport.ALLOWED_TIME_UNITS;
import static de.monticore.siunit.util.SIUnitLiteralSupport.numericValue;
import static de.monticore.siunit.util.SIUnitLiteralSupport.simpleUnit;

/** Ensures that every-time intervals and repetition counts are valid. */
public class EveryTimeConditionHasValidRepetitions implements
    TimeTriggerConditionsASTEveryTimeConditionCoCo {
  
  public static final String INVALID_INTERVAL = "0xF0009";
  public static final String INVALID_START = "0xF000A";
  public static final String NON_POSITIVE_REPETITIONS = "0xF000B";
  public static final Set<String> ALLOWED_UNITS = ALLOWED_TIME_UNITS;
  
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
    Optional<BigDecimal> value = numericValue(duration.getNumericLiteral());
    Optional<String> unit = simpleUnit(duration.getSIUnit());
    
    if (value.isEmpty()) {
      report(node, INVALID_INTERVAL, "Every timer interval numeric value is invalid");
      return;
    }
    if (value.get().signum() <= 0) {
      report(node, INVALID_INTERVAL, "Every timer interval must be greater than 0");
    }
    if (unit.isEmpty() || !ALLOWED_UNITS.contains(unit.get())) {
      report(node, INVALID_INTERVAL,
          "Every timer interval unit must be one of ms, s, min, h, or d");
    }
  }
  
  protected void validateStart(ASTISODateTime start, ASTEveryTimeCondition node) {
    try {
      ISOTemporalsConversions.toOffsetDateTime(start);
    }
    catch (java.time.format.DateTimeParseException exception) {
      report(node, INVALID_START,
          "Every timer start must be a valid ISO date-time with an explicit UTC offset");
    }
  }
  
  protected void validateRepetitions(ASTNatLiteral literal, ASTEveryTimeCondition node) {
    Optional<BigDecimal> value = numericValue(literal);
    if (value.isEmpty()) {
      report(node, NON_POSITIVE_REPETITIONS,
          "Every timer repetition count must be a positive integer");
      return;
    }
    if (value.get().signum() <= 0) {
      report(node, NON_POSITIVE_REPETITIONS, "Every timer repetition count must be greater than 0");
    }
  }
  
  protected void report(ASTEveryTimeCondition node, String code, String requirement) {
    Log.error(code + " " + requirement + ".", node.get_SourcePositionStart());
  }
  
}
