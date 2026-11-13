/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Factory for the built-in temporal SymTypes
 * (ONLY) convenience methods for
 * {@link SymTypeExpressionFactory}.
 * The TypeSymbols are added by {@link TemporalTypes#init()}.
 */
public class TemporalSymTypeFactory {

  public static SymTypeExpression createTimePoint() {
    return createTemporalType(TemporalSymTypeRelations.TIME_POINT);
  }

  public static SymTypeExpression createDayTime() {
    return createTemporalType(TemporalSymTypeRelations.DAY_TIME);
  }

  public static SymTypeExpression createPeriod() {
    return createTemporalType(TemporalSymTypeRelations.PERIOD);
  }

  /**
   * @return the type of exact lengths of time, i.e., {@code [s]<long>}
   */
  public static SymTypeExpression createDuration() {
    return SymTypeExpressionFactory.createNumericWithSIUnit(
        SymTypeExpressionFactory.createSIUnit(
            List.of(SymTypeExpressionFactory.createSIUnitBasic("s")),
            List.of()
        ),
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG)
    );
  }

  // Helper

  protected static SymTypeExpression createTemporalType(String fullName) {
    Optional<TypeSymbol> typeSymbol =
        BasicSymbolsMill.globalScope().resolveType(fullName);
    if (typeSymbol.isPresent()) {
      return SymTypeExpressionFactory.createTypeObject(typeSymbol.get());
    }
    Log.error("0xFDE01 unable to resolve the temporal type "
        + fullName + ", was TemporalTypes.init() called?");
    return SymTypeExpressionFactory.createObscureType();
  }

}
