// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Relations of SymTypeExpressions that are iterable (e.g., arrays, List, Iterable)
 */
public class SymTypeRelationsOfIterables {

  protected static SymTypeRelationsOfIterables delegate;

  /**
   * Whether the type is an array (e.g., int[])
   *
   * @see SymTypeExpression#isArrayType()
   */
  public static boolean isArrayType(SymTypeExpression type) {
    return getDelegate()._isArrayType(type);
  }

  protected boolean _isArrayType(SymTypeExpression type) {
    return type.isArrayType();
  }

  /**
   * Whether the type is of the raw type Iterable (e.g., java.lang.Iterable)
   *
   * @see SymTypeExpression#isArrayType()
   */
  protected static boolean isOfTypeIterable(SymTypeExpression type) {
    return getDelegate()._isOfTypeIterable(type);
  }

  protected boolean _isOfTypeIterable(SymTypeExpression type) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = type.asGenericType();
    String name = generic.getTypeConstructorFullName();
    if (!name.equals("Iterable") && !name.equals("java.lang.Iterable")) {
      return false;
    }
    return generic.sizeArguments() == 1;
  }

  /**
   * Whether the type if of or a subtype of the raw type Iterable (e.g., java.lang.Iterable)
   *
   * @see SymTypeExpression#isArrayType()
   */
  protected static boolean isOfTypeIterableOrSubType(SymTypeExpression type) {
    return getDelegate()._isOfTypeIterable(type);
  }

  protected boolean _isOfTypeIterableOrSubType(SymTypeExpression type) {
    if (_isOfTypeIterable(type)) {
      return true;
    } else {
      for (SymTypeExpression superType : SymTypeRelations.getNominalSuperTypes(type)) {
        if (_isOfTypeIterableOrSubType(superType)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return an {@code Optional} of the Element type of the iterable
   * or an {@link Optional#empty()} if the type is not iterable
   */
  public static Optional<SymTypeExpression> getIterationType(SymTypeExpression type) {
    return getDelegate()._getIterationType(type);
  }

  protected Optional<SymTypeExpression> _getIterationType(SymTypeExpression type) {
    Optional<SymTypeExpression> iterationType = _getIterationTypeOfArray(type);

    if (iterationType.isPresent()) {
      return iterationType;
    }

    return _getIterationTypeOfTypeIterable(type);
  }

  protected Optional<SymTypeExpression> _getIterationTypeOfArray(SymTypeExpression type) {
    if (_isArrayType(type)) {
      return Optional.of(type.asArrayType().cloneWithLessDim(1));
    } else {
      return Optional.empty();
    }
  }

  protected Optional<SymTypeExpression> _getIterationTypeOfTypeIterable(SymTypeExpression type) {
    if (_isOfTypeIterable(type)) {
      return Optional.of(type.asGenericType().getArgument(0));
    }
    for (SymTypeExpression superType : SymTypeRelations.getNominalSuperTypes(type)) {
      Optional<SymTypeExpression> iterationType = _getIterationTypeOfTypeIterable(superType);
      if (iterationType.isPresent()) {
        return iterationType;
      }
    }
    return Optional.empty();
  }

  // static delegate

  public static void init() {
    Log.trace("init default IterableSymTypeRelations", "TypeCheck setup");
    setDelegate(new SymTypeRelationsOfIterables());
  }

  public static void reset() {
    SymTypeRelationsOfIterables.delegate = null;
  }

  protected static void setDelegate(SymTypeRelationsOfIterables newDelegate) {
    SymTypeRelationsOfIterables.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static SymTypeRelationsOfIterables getDelegate() {
    if (SymTypeRelationsOfIterables.delegate == null) {
      init();
    }
    return SymTypeRelationsOfIterables.delegate;
  }

}
