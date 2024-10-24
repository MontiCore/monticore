package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Offers functions to lift functions that occur in the type check visitors.
 * E.g. it can lift functions to handle Obscure,
 * instead of having every function handle Obscure itself.
 * Usually lifting allows the function being lifted
 * to be used in a more general setting,
 * e.g., from a setting without Obscure to a setting with Obscure.
 */
public class TypeVisitorLifting {

  protected static TypeVisitorLifting delegate;

  public static void init() {
    Log.trace("initializing default TypeVisitorLifting", "TypeCheck setup");
    TypeVisitorLifting.delegate = new TypeVisitorLifting();
  }

  static {
    init();
  }

  /**
   * The default lifting, applicable in most cases.
   * It tries to transparently handle Obscure, Unions, and non-normalized types.
   */
  public static Function<SymTypeExpression, SymTypeExpression> liftDefault(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftDefault(func);
  }

  protected Function<SymTypeExpression, SymTypeExpression> calculateLiftDefault(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return liftForNonNormalized(liftForObscure(liftForUnion(func)));
  }

  public static BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> liftDefault(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftDefault(func);
  }

  protected BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> calculateLiftDefault(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return liftForNonNormalized(liftForObscure(liftForUnion(func)));
  }

  /**
   * Lifts a function to handle Obscure by returning Obscure whenever it is encountered.
   */
  public static Function<SymTypeExpression, SymTypeExpression> liftForObscure(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForObscure(func);
  }

  protected Function<SymTypeExpression, SymTypeExpression> calculateLiftForObscure(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType) -> {
      if (symType.isObscureType()) {
        // error already logged
        return SymTypeExpressionFactory.createObscureType();
      }
      return func.apply(symType);
    };
  }

  public static BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> liftForObscure(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForObscure(func);
  }

  protected BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> calculateLiftForObscure(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType1, SymTypeExpression symType2) -> {
      if (symType1.isObscureType() || symType2.isObscureType()) {
        // error already logged
        return SymTypeExpressionFactory.createObscureType();
      }
      return func.apply(symType1, symType2);
    };
  }

  /**
   * Lifts a function to handle (top-level) unions.
   * Each type in the union is calculated on its own
   * and the results are then stored in a union again.
   */
  public static Function<SymTypeExpression, SymTypeExpression> liftForUnion(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForUnion(func);
  }

  protected Function<SymTypeExpression, SymTypeExpression> calculateLiftForUnion(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType) -> {
      if (symType.isUnionType()) {
        Set<SymTypeExpression> results = new HashSet<>();
        for (SymTypeExpression unionizedType : symType.asUnionType().getUnionizedTypeSet()) {
          results.add(func.apply(unionizedType));
        }
        return createUnionIfApplicable(results);
      }
      return func.apply(symType);
    };
  }

  public static BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> liftForUnion(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForUnion(func);
  }

  protected BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> calculateLiftForUnion(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType1, SymTypeExpression symType2) -> {
      Set<SymTypeExpression> arguments1;
      Set<SymTypeExpression> arguments2;
      Set<SymTypeExpression> results = new HashSet<>();
      if (symType1.isUnionType()) {
        arguments1 = symType1.asUnionType().getUnionizedTypeSet();
      }
      else {
        arguments1 = Set.of(symType1);
      }
      if (symType2.isUnionType()) {
        arguments2 = symType2.asUnionType().getUnionizedTypeSet();
      }
      else {
        arguments2 = Set.of(symType2);
      }
      for (SymTypeExpression argument1 : arguments1) {
        for (SymTypeExpression argument2 : arguments2) {
          results.add(func.apply(argument1, argument2));
        }
      }
      return createUnionIfApplicable(results);
    };
  }

  /**
   * Lifts Functions to handle non-normalized inputs by normalizing them.
   */
  public static Function<SymTypeExpression, SymTypeExpression> liftForNonNormalized(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForNonNormalized(func);
  }

  protected Function<SymTypeExpression, SymTypeExpression> calculateLiftForNonNormalized(
      Function<SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType) -> {
      return func.apply(SymTypeRelations.normalize(symType));
    };
  }

  public static BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> liftForNonNormalized(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return delegate.calculateLiftForNonNormalized(func);
  }

  protected BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> calculateLiftForNonNormalized(
      BiFunction<SymTypeExpression, SymTypeExpression, SymTypeExpression> func) {
    return (SymTypeExpression symType1, SymTypeExpression symType2) -> {
      return func.apply(SymTypeRelations.normalize(symType1), SymTypeRelations.normalize(symType2));
    };
  }

  // Helper

  protected SymTypeExpression createUnionIfApplicable(
      Collection<? extends SymTypeExpression> unionizedTypes) {
    // no matter the value, the result has to be possible to be calculated,
    // thus, we cannot accept obscure in the union
    // (which would usually be the case)
    if (unionizedTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      return SymTypeExpressionFactory.createObscureType();
    }
    if (unionizedTypes.isEmpty()) {
      return SymTypeExpressionFactory.createObscureType();
    }
    else if (unionizedTypes.size() == 1) {
      return unionizedTypes.stream().findFirst().get();
    }
    else {
      return SymTypeExpressionFactory.createUnion(unionizedTypes);
    }
  }
}
