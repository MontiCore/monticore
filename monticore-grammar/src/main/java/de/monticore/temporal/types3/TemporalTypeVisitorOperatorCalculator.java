/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.temporal.types3.TemporalSymTypeRelations.isDayTime;
import static de.monticore.temporal.types3.TemporalSymTypeRelations.isDuration;
import static de.monticore.temporal.types3.TemporalSymTypeRelations.isInstant;
import static de.monticore.temporal.types3.TemporalSymTypeRelations.isPeriod;
import static de.monticore.temporal.types3.TemporalSymTypeRelations.isTimePoint;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createStringType;

/**
 * Extends the operators of {@link TypeVisitorOperatorCalculator}
 * by the operators of the temporal types TimePoint, DayTime, and Period,
 * as well as Duration (any SIUnit type of the dimension time):
 * <ul>
 *   <li>{@code TimePoint ± Period -> TimePoint} (and {@code Period + TimePoint})</li>
 *   <li>{@code TimePoint ± Duration -> TimePoint},
 *       {@code DayTime ± Duration -> DayTime} (and {@code Duration + ...})</li>
 *   <li>{@code TimePoint - TimePoint -> [s]<long>},
 *       {@code DayTime - DayTime -> [s]<long>}</li>
 *   <li>{@code Period ± Period}, {@code Period ± Duration},
 *       {@code Duration ± Period}, and {@code -Period} result in Period</li>
 *   <li>{@code Period * n}, {@code n * Period}, {@code Period / n}
 *       result in Period for numeric n</li>
 *   <li>{@code <, <=, >, >=} on two TimePoints, DayTimes, or Periods</li>
 * </ul>
 * Further, TimePoints, DayTimes, and Periods can be concatenated with Strings.
 * Equality is already supported by the base class,
 * Durations are supported by the base class (as SIUnit types).
 * <p>
 * Use {@link #init()} instead of {@link TypeVisitorOperatorCalculator#init()}.
 */
public class TemporalTypeVisitorOperatorCalculator
    extends TypeVisitorOperatorCalculator {

  // arithmetic: +, -, *, /

  @Override
  protected SymTypeExpression calculatePlus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (SymTypeRelations.isStringOrSubType(left)
        || SymTypeRelations.isStringOrSubType(right)
        || !isTemporal(left, right)) {
      return super.calculatePlus(left, right);
    }
    if (isTimePoint(left) && (isPeriod(right) || isDuration(right))) {
      return left;
    }
    if (isTimePoint(right) && (isPeriod(left) || isDuration(left))) {
      return right;
    }
    if (isDayTime(left) && isDuration(right)) {
      return left;
    }
    if (isDayTime(right) && isDuration(left)) {
      return right;
    }
    if (isPeriodOrDuration(left) && isPeriodOrDuration(right)) {
      return TemporalSymTypeFactory.createPeriod();
    }
    return createObscureType();
  }

  /**
   * TimePoints, DayTimes, and Periods can be concatenated with Strings,
   * e.g., {@code "today is " + d"2017-12-04"}.
   */
  @Override
  protected SymTypeExpression calculateToString(SymTypeExpression type) {
    if (isTemporal(type)) {
      return createStringType();
    }
    return super.calculateToString(type);
  }

  @Override
  protected Optional<SymTypeExpression> _minus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateMinus)
            .apply(left, right);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateMinus(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (!isTemporal(left, right)) {
      return calculatePlusMinusModulo(left, right);
    }
    if (isTimePoint(left) && (isPeriod(right) || isDuration(right))) {
      return left;
    }
    if (isDayTime(left) && isDuration(right)) {
      return left;
    }
    if ((isTimePoint(left) && isTimePoint(right))
        || (isDayTime(left) && isDayTime(right))) {
      return TemporalSymTypeFactory.createDuration();
    }
    if (isPeriodOrDuration(left) && isPeriodOrDuration(right)) {
      return TemporalSymTypeFactory.createPeriod();
    }
    return createObscureType();
  }

  @Override
  protected Optional<SymTypeExpression> _modulo(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (isTemporal(left, right)) {
      return Optional.empty();
    }
    return super._modulo(left, right);
  }

  @Override
  protected SymTypeExpression calculateMultiply(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (!isTemporal(left, right)) {
      return super.calculateMultiply(left, right);
    }
    if (isPeriod(left) && SymTypeRelations.isNumericType(right)) {
      return left;
    }
    if (isPeriod(right) && SymTypeRelations.isNumericType(left)) {
      return right;
    }
    return createObscureType();
  }

  @Override
  protected SymTypeExpression calculateDivide(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (!isTemporal(left, right)) {
      return super.calculateDivide(left, right);
    }
    if (isPeriod(left) && SymTypeRelations.isNumericType(right)) {
      return left;
    }
    return createObscureType();
  }

  // numeric prefixes: +, -

  @Override
  protected Optional<SymTypeExpression> _minusPrefix(SymTypeExpression inner) {
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(this::calculateMinusPrefix)
            .apply(inner);
    return obscure2Empty(result);
  }

  protected SymTypeExpression calculateMinusPrefix(SymTypeExpression inner) {
    if (isPeriod(inner)) {
      return inner;
    }
    if (isInstant(inner)) {
      return createObscureType();
    }
    return calculatePlusMinusPrefix(inner);
  }

  // numeric comparison: <, <=, >, >=

  @Override
  protected SymTypeExpression calculateNumericComparison(
      SymTypeExpression left,
      SymTypeExpression right
  ) {
    if (!isTemporal(left, right)) {
      return super.calculateNumericComparison(left, right);
    }
    if ((isTimePoint(left) && isTimePoint(right))
        || (isDayTime(left) && isDayTime(right))
        || (isPeriod(left) && isPeriod(right))) {
      return createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return createObscureType();
  }

  // Helper

  /**
   * @return whether any of the types is TimePoint, DayTime, or Period
   */
  protected boolean isTemporal(SymTypeExpression... types) {
    for (SymTypeExpression type : types) {
      if (isInstant(type) || isPeriod(type)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isPeriodOrDuration(SymTypeExpression type) {
    return isPeriod(type) || isDuration(type);
  }

  // static delegate

  public static void init() {
    Log.trace("init TemporalTypeVisitorOperatorCalculator", "TypeCheck setup");
    setDelegate(new TemporalTypeVisitorOperatorCalculator());
  }

}
