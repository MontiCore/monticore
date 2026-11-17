// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.temporal;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Factory for the temporal types
 * (ONLY) convenience methods for
 * {@link SymTypeExpressionFactory}
 * <p>
 * The types TimePoint, DayTime, and Period are nominal types
 * in the package de.monticore.temporal,
 * whose symbols are expected to be resolvable from the global scope,
 * e.g., using
 * {@link de.monticore.temporal.temporalbasis.TemporalBasisMill#initializeTemporalSymbols()}.
 * Durations are SIUnit types that can be normalized to seconds,
 * e.g., {@code [s]<double>} or {@code [h]<int>}.
 */
public class TemporalSymTypeFactory {

  protected static TemporalSymTypeFactory delegate;

  public static SymTypeExpression createTimePoint() {
    return getDelegate()._createTimePoint();
  }

  protected SymTypeExpression _createTimePoint() {
    return createTemporalType(TemporalSymTypeRelations.TIME_POINT);
  }

  public static SymTypeExpression createDayTime() {
    return getDelegate()._createDayTime();
  }

  protected SymTypeExpression _createDayTime() {
    return createTemporalType(TemporalSymTypeRelations.DAY_TIME);
  }

  public static SymTypeExpression createPeriod() {
    return getDelegate()._createPeriod();
  }

  protected SymTypeExpression _createPeriod() {
    return createTemporalType(TemporalSymTypeRelations.PERIOD);
  }

  /**
   * @return the SIUnit [s]
   */
  public static SymTypeOfSIUnit createDuration() {
    return getDelegate()._createDuration();
  }

  protected SymTypeOfSIUnit _createDuration() {
    return SymTypeExpressionFactory.createSIUnit(
        List.of(SymTypeExpressionFactory.createSIUnitBasic("s")),
        Collections.emptyList()
    );
  }

  /**
   * @return a Duration with the given numeric type, e.g., {@code [s]<long>}
   */
  public static SymTypeOfNumericWithSIUnit createDuration(
      SymTypeExpression numericType
  ) {
    return getDelegate()._createDuration(numericType);
  }

  protected SymTypeOfNumericWithSIUnit _createDuration(
      SymTypeExpression numericType
  ) {
    return SymTypeExpressionFactory
        .createNumericWithSIUnit(createDuration(), numericType);
  }

  // Helper

  protected SymTypeExpression createTemporalType(String name) {
    Optional<TypeSymbol> typeSymbol =
        BasicSymbolsMill.globalScope().resolveType(name);
    if (typeSymbol.isPresent()) {
      return SymTypeExpressionFactory.createTypeObject(typeSymbol.get());
    }
    else {
      Log.error("0xFD3A0 unable to resolve type " + name
          + ", unable to create the temporal type."
          + " Did you add the temporal types to your language?"
      );
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  // static delegate

  public static void init() {
    Log.trace("init default TemporalSymTypeFactory", "TypeCheck setup");
    setDelegate(new TemporalSymTypeFactory());
  }

  public static void reset() {
    TemporalSymTypeFactory.delegate = null;
  }

  protected static void setDelegate(TemporalSymTypeFactory newDelegate) {
    TemporalSymTypeFactory.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static TemporalSymTypeFactory getDelegate() {
    if (TemporalSymTypeFactory.delegate == null) {
      init();
    }
    return TemporalSymTypeFactory.delegate;
  }

}
