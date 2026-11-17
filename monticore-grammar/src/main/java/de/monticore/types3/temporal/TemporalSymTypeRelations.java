// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.temporal;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

/**
 * Relations over the temporal types, e.g., {@code isTimePoint}.
 * <p>
 * The temporal types are TimePoint, DayTime, Period, and Duration.
 * The full names of the nominal types are qualified,
 * e.g., de.monticore.temporal.TimePoint.
 * TimePoint and DayTime are the instant types,
 * Period and Duration are the span types.
 * Duration is not a nominal type, but any SIUnit type
 * that can be normalized to seconds, e.g., {@code [s]<int>} or {@code [h]<double>}.
 */
public class TemporalSymTypeRelations {

  /**
   * the package of the nominal temporal types
   */
  public static final String PACKAGE = "de.monticore.temporal";

  public static final String TIME_POINT = PACKAGE + ".TimePoint";
  public static final String DAY_TIME = PACKAGE + ".DayTime";
  public static final String PERIOD = PACKAGE + ".Period";

  protected static TemporalSymTypeRelations delegate;

  /**
   * @return whether the type is a TimePoint, DayTime, or Period.
   *     Durations are not included, as they are SIUnit types.
   */
  public static boolean isTemporalType(SymTypeExpression type) {
    return getDelegate()._isTemporalType(type);
  }

  protected boolean _isTemporalType(SymTypeExpression type) {
    return isInstant(type) || isPeriod(type);
  }

  // instant types

  /**
   * @return whether the type is a TimePoint or a DayTime
   */
  public static boolean isInstant(SymTypeExpression type) {
    return getDelegate()._isInstant(type);
  }

  protected boolean _isInstant(SymTypeExpression type) {
    return isTimePoint(type) || isDayTime(type);
  }

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

  // span types

  public static boolean isPeriod(SymTypeExpression type) {
    return getDelegate()._isPeriod(type);
  }

  protected boolean _isPeriod(SymTypeExpression type) {
    return isSpecificTemporalType(type, PERIOD);
  }

  /**
   * @return whether the type is an SIUnit type
   *     that can be normalized to seconds, e.g., {@code [min]<int>}
   */
  public static boolean isDuration(SymTypeExpression type) {
    return getDelegate()._isDuration(type);
  }

  protected boolean _isDuration(SymTypeExpression type) {
    SymTypeOfSIUnit siUnit;
    if (type.isSIUnitType()) {
      siUnit = type.asSIUnitType();
    }
    else if (type.isNumericWithSIUnitType()) {
      siUnit = type.asNumericWithSIUnitType().getSIUnitType();
    }
    else {
      return false;
    }
    return SymTypeRelations.isCompatible(
        TemporalSymTypeFactory.createDuration(), siUnit
    );
  }

  // Helper

  protected boolean isSpecificTemporalType(
      SymTypeExpression type,
      String name
  ) {
    return type.isObjectType() && type.printFullName().equals(name);
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
