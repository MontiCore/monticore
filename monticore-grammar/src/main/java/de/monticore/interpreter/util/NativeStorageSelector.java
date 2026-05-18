// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;

import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isIntegralType;
import static de.monticore.types3.SymTypeRelations.isNumericType;

/**
 * selects which model types are stored
 * within which Java types in the interpreter.
 * This is mostly important wrt.
 * {@link de.monticore.interpreter.frames.MIFrame},
 * {@link de.monticore.interpreter.calculations.MICalculation}, and
 * {@link de.monticore.interpreter.setters.MISetter}.
 * <p>
 * There are four native storage options,
 * {@code boolean}, {@code int}, {@code double}, {@code Object},
 * there the {@code Object} is always an
 * {@link de.monticore.interpreter.values.MIValue}.
 * <p>
 * The corresponding methods are
 * {@link #isStoredAsBoolean(SymTypeExpression)},
 * {@link #isStoredAsInt(SymTypeExpression)},
 * {@link #isStoredAsDouble(SymTypeExpression)}, and
 * {@link #isStoredAsObject(SymTypeExpression)}, respectively.
 * Edge case: {@link #hasNothingToStore(SymTypeExpression)}
 * exists for the {@code void}-case.
 * <p>
 * Explanation: Boxing is very pricy and reduces inlining capabilities.
 * As such, we opted to have certain model types
 * be stored as primitive java types.
 */
public class NativeStorageSelector {

  /**
   * Whether the model type is stored natively as a {@code boolean}.
   *
   * @param type the model type
   * @return whether it is to be stored as a {@code boolean}.
   */
  public static boolean isStoredAsBoolean(SymTypeExpression type) {
    return isBoolean(type);
  }

  /**
   * Whether the model type is stored natively as an {@code int}.
   *
   * @param type the model type
   * @return whether it is to be stored as an {@code int}.
   */
  public static boolean isStoredAsInt(SymTypeExpression type) {
    return isIntegralType(type);
  }

  /**
   * Whether the model type is stored natively as a {@code double}.
   *
   * @param type the model type
   * @return whether it is to be stored as a {@code double}.
   */
  public static boolean isStoredAsDouble(SymTypeExpression type) {
    return !isStoredAsInt(type) && isNumericType(type);
  }

  /**
   * Whether the model type is stored natively as an {@code Object}.
   *
   * @param type the model type
   * @return whether it is to be stored as a {@code Object}.
   */
  public static boolean isStoredAsObject(SymTypeExpression type) {
    return !isStoredAsBoolean(type)
        && !isStoredAsInt(type)
        && !isStoredAsDouble(type)
        && !hasNothingToStore(type);
  }

  /**
   * Whether the model type is not stored natively.
   *
   * @param type the model type
   * @return whether it is not to be stored at all.
   */
  public static boolean hasNothingToStore(SymTypeExpression type) {
    return type.isVoidType();
  }

  // switch versions based on native storage

  public static <T> T switchByFormat(VariableSymbol symbol,
      T booleanCase, T intCase, T doubleCase, T objectCase
  ) {
    SymTypeExpression varType = getTypeOfVar(symbol);
    return switchByFormat(
        varType, booleanCase, intCase, doubleCase, objectCase
    );
  }

  public static <T> T switchByFormat(
      SymTypeExpression type,
      T booleanCase,
      T intCase,
      T doubleCase,
      T objectCase
  ) {
    if (type.isVoidType()) {
      throw new IllegalArgumentException("void is not allowed here");
    }
    return switchByFormat(
        type, booleanCase, intCase, doubleCase, objectCase, null
    );
  }

  public static <T> T switchByFormat(
      SymTypeExpression type,
      T booleanCase,
      T intCase,
      T doubleCase,
      T objectCase,
      T voidCase
  ) {
    if (isStoredAsBoolean(type)) {
      return booleanCase;
    }
    else if (isStoredAsInt(type)) {
      return intCase;
    }
    else if (isStoredAsDouble(type)) {
      return doubleCase;
    }
    else if (hasNothingToStore(type)) {
      return voidCase;
    }
    else {
      return objectCase;
    }
  }

  // helper

  protected static SymTypeExpression getTypeOfVar(VariableSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkNotNull(symbol.getType());
    return SymTypeRelations.normalize(symbol.getType());
  }
}
