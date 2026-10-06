/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnitBasic;

/**
 * Relations for the built-in temporal SymTypes:
 * TimePoint, DayTime, and Period (see {@link TemporalTypes}),
 * as well as Duration, which is not a type of its own,
 * but any SIUnit type (with or without a numeric type)
 * of the dimension time (e.g., [s], [min], [d]).
 */
public class TemporalSymTypeRelations {

  public static final String PACKAGE = "de.monticore.temporal";

  public static final String TIME_POINT_NAME = "TimePoint";
  public static final String DAY_TIME_NAME = "DayTime";
  public static final String PERIOD_NAME = "Period";

  public static final String TIME_POINT = PACKAGE + "." + TIME_POINT_NAME;
  public static final String DAY_TIME = PACKAGE + "." + DAY_TIME_NAME;
  public static final String PERIOD = PACKAGE + "." + PERIOD_NAME;

  protected static TemporalSymTypeRelations delegate;

  // methods

  public static boolean isTimePoint(SymTypeExpression type) {
    return getDelegate()._isTimePoint(type);
  }

  protected boolean _isTimePoint(SymTypeExpression type) {
    return isSpecificTemporalType(type, TIME_POINT);
  }

  public static boolean isDayTime(SymTypeExpression type) {
    return getDelegate()._isDayTime(type);
  }

  protected boolean _isDayTime(SymTypeExpression type) {
    return isSpecificTemporalType(type, DAY_TIME);
  }

  public static boolean isPeriod(SymTypeExpression type) {
    return getDelegate()._isPeriod(type);
  }

  protected boolean _isPeriod(SymTypeExpression type) {
    return isSpecificTemporalType(type, PERIOD);
  }

  /**
   * @return whether the type is TimePoint or DayTime
   */
  public static boolean isInstant(SymTypeExpression type) {
    return getDelegate()._isInstant(type);
  }

  protected boolean _isInstant(SymTypeExpression type) {
    return isTimePoint(type) || isDayTime(type);
  }

  /**
   * @return whether the type is an SIUnit type of the dimension time,
   *     e.g., {@code [s]}, {@code [min]<int>}, or {@code [d]<double>}.
   */
  public static boolean isDuration(SymTypeExpression type) {
    return getDelegate()._isDuration(type);
  }

  protected boolean _isDuration(SymTypeExpression type) {
    Optional<SymTypeOfSIUnit> siUnit = getSIUnit(SymTypeRelations.normalize(type));
    if (siUnit.isEmpty()) {
      return false;
    }
    SymTypeOfSIUnit seconds = createSIUnit(List.of(createSIUnitBasic("s")), List.of());
    return SIUnitTypeRelations.isOfDimensionOne(
        SIUnitTypeRelations.multiply(siUnit.get(), SIUnitTypeRelations.invert(seconds))
    );
  }

  // Helper

  protected boolean isSpecificTemporalType(SymTypeExpression type, String fullName) {
    SymTypeExpression normalized = SymTypeRelations.normalize(type);
    return normalized.isObjectType()
        && normalized.printFullName().equals(fullName);
  }

  protected Optional<SymTypeOfSIUnit> getSIUnit(SymTypeExpression type) {
    if (type.isSIUnitType()) {
      return Optional.of(type.asSIUnitType());
    }
    else if (type.isNumericWithSIUnitType()) {
      return Optional.of(type.asNumericWithSIUnitType().getSIUnitType());
    }
    return Optional.empty();
  }

  // static delegate

  public static void init() {
    Log.trace("init default TemporalSymTypeRelations", "TypeCheck setup");
    setDelegate(new TemporalSymTypeRelations());
  }

  public static void reset() {
    TemporalSymTypeRelations.delegate = null;
  }

  protected static void setDelegate(TemporalSymTypeRelations newDelegate) {
    TemporalSymTypeRelations.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static TemporalSymTypeRelations getDelegate() {
    if (TemporalSymTypeRelations.delegate == null) {
      init();
    }
    return TemporalSymTypeRelations.delegate;
  }

}
