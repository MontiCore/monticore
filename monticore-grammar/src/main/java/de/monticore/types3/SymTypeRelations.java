/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.util.SymTypeRelationsDefaultDelegatee;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Relations of SymTypeExpressions
 * <p>
 * some are dependent on the specific type system
 * and as such not hardcoded in the SymTypeExpressions themselves
 * <p>
 * Default implementation in
 * {@link SymTypeRelationsDefaultDelegatee}
 */
public abstract class SymTypeRelations {

  protected static SymTypeRelations delegate;

  // methods

  /**
   * whether the target can be assigned to by the source,
   * e.g., assignment operator: x = 2,
   * type of x and type of 2 need to be compatible,
   * e.g., function call: {@code (float -> void)(2)},
   * float and type of 2 need to be compatible.
   */
  public static boolean isCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    return getDelegate()._isCompatible(target, source);
  }

  protected abstract boolean _isCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  );

  /**
   * Whether subType is the sub-type of superType,
   * Examples:
   * isSubType(Person, Person)
   * isSubType(Student, Person)
   * !isSubType(Person, Student)
   * isSubType(int, float)
   * !isSubType(float, int)
   */
  public static boolean isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return getDelegate()._isSubTypeOf(subType, superType);
  }

  protected abstract boolean _isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  );

  /**
   * returns nominal supertypes.
   * Nominal supertypes are those that are explicitly listed as super types,
   * e.g., in Java those specified using "extends" or "implements".
   * The return value is neither the reflexive nor the transitive closure,
   * i.e., only the direct supertypes are included (s. Java spec 20 4.10).
   * Note that the "direct" supertype-relation is deliberately underspecified,
   * such that it can be refined according to the specific type system's needs.
   */
  public static List<SymTypeExpression> getNominalSuperTypes(
      SymTypeExpression thisType
  ) {
    return getDelegate()._getNominalSuperTypes(thisType);
  }

  protected abstract List<SymTypeExpression> _getNominalSuperTypes(
      SymTypeExpression thisType
  );

  /**
   * least upper bound for a set of types
   * DISREGARDING the union of the types;
   * The least upper bound for a set of types is always the union of the same,
   * this will create the least upper bound that is not the union.
   * <p>
   * For, e.g., union types
   * unlike the Java counterpart,
   * we specify it for non-reference types as well,
   * making it more akin to Java conditional expressions,
   * where "a?b:c" has type leastUpperBound(b,c)
   * <p>
   * todo should just return top?
   * todo need to rename to e.g. simplifiedLeastUpperBound
   * <a href="https://git.rwth-aachen.de/monticore/monticore/-/issues/4187">monticore#4187</a>
   * empty represents the universal type (aka the lack of a bound)
   * Obscure is returned, if no lub could be calculated, e.g. lub(int, Person)
   */
  public static Optional<SymTypeExpression> leastUpperBound(
      Collection<SymTypeExpression> types
  ) {
    return getDelegate()._leastUpperBound(types);
  }

  public static Optional<SymTypeExpression> leastUpperBound(
      SymTypeExpression... types
  ) {
    return leastUpperBound(List.of(types));
  }

  protected abstract Optional<SymTypeExpression> _leastUpperBound(
      Collection<SymTypeExpression> types
  );

  /**
   * Boxes SymTypeExpressions,
   * including, but not limited to, Java primitive boxing
   * e.g., {@code int -> java.lang.Integer}
   * e.g., {@code List -> java.util.List}
   */
  public static SymTypeExpression box(SymTypeExpression unboxed) {
    return getDelegate()._box(unboxed);
  }

  protected abstract SymTypeExpression _box(SymTypeExpression unboxed);

  /**
   * Unboxes SymTypeExpressions,
   * including, but not limited to, Java primitive unboxing
   * e.g., {@code java.lang.Integer -> int}
   * e.g., {@code java.util.List -> List}
   */
  public static SymTypeExpression unbox(SymTypeExpression boxed) {
    return getDelegate()._unbox(boxed);
  }

  protected abstract SymTypeExpression _unbox(SymTypeExpression boxed);

  /**
   * normalizes the SymTypeExpression,
   * e.g., {@code (A & B[])[] -> (A[] & B[][])}
   * <p>
   * Within our type systems, each type has ONE normalized representation.
   * This can be used to, e.g., compare SymTypeExpressions
   */
  public static SymTypeExpression normalize(SymTypeExpression type) {
    return getDelegate()._normalize(type);
  }

  protected abstract SymTypeExpression _normalize(SymTypeExpression type);

  // primitives

  /**
   * calculates the one promoted numeric type,
   * ignoring the specifics of the context
   * s. Java spec. 20 5.6
   * e.g., {@code short -> int}
   * e.g., {@code byte, float -> float}
   */
  public static SymTypeExpression numericPromotion(
      List<SymTypeExpression> types
  ) {
    return getDelegate()._numericPromotion(types);
  }

  protected abstract SymTypeExpression _numericPromotion(
      List<SymTypeExpression> types
  );

  public static SymTypeExpression numericPromotion(
      SymTypeExpression... types
  ) {
    return numericPromotion(List.of(types));
  }

  /**
   * tests if the expression is of numeric type,
   * e.g., in Java: (double, float, long, int, char, short, byte)
   */
  public static boolean isNumericType(SymTypeExpression type) {
    return getDelegate()._isNumericType(type);
  }

  protected abstract boolean _isNumericType(SymTypeExpression type);

  /**
   * tests if the expression is of integral type,
   * e.g., in Java: (long, int, char, short, byte)
   */
  public static boolean isIntegralType(SymTypeExpression type) {
    return getDelegate()._isIntegralType(type);
  }

  protected abstract boolean _isIntegralType(SymTypeExpression type);

  public static boolean isBoolean(SymTypeExpression type) {
    return getDelegate()._isBoolean(type);
  }

  protected abstract boolean _isBoolean(SymTypeExpression type);

  public static boolean isInt(SymTypeExpression type) {
    return getDelegate()._isInt(type);
  }

  protected abstract boolean _isInt(SymTypeExpression type);

  public static boolean isDouble(SymTypeExpression type) {
    return getDelegate()._isDouble(type);
  }

  protected abstract boolean _isDouble(SymTypeExpression type);

  public static boolean isFloat(SymTypeExpression type) {
    return getDelegate()._isFloat(type);
  }

  protected abstract boolean _isFloat(SymTypeExpression type);

  public static boolean isLong(SymTypeExpression type) {
    return getDelegate()._isLong(type);
  }

  protected abstract boolean _isLong(SymTypeExpression type);

  public static boolean isChar(SymTypeExpression type) {
    return getDelegate()._isChar(type);
  }

  protected abstract boolean _isChar(SymTypeExpression type);

  public static boolean isShort(SymTypeExpression type) {
    return getDelegate()._isShort(type);
  }

  protected abstract boolean _isShort(SymTypeExpression type);

  public static boolean isByte(SymTypeExpression type) {
    return getDelegate()._isByte(type);
  }

  protected abstract boolean _isByte(SymTypeExpression type);

  /**
   * This is most likely NOT the method you need;
   * This returns whether the type is _exactly_ String.
   * In most cases, you want to check whether the type
   * is either compatible to, or a subtype of String.
   * You may want to use
   * {@link #isStringOrSubType(SymTypeExpression)} instead.
   */
  public static boolean isString(SymTypeExpression type) {
    return getDelegate()._isString(type);
  }

  protected abstract boolean _isString(SymTypeExpression type);

  /**
   * @param type the SymTypeExpression to check
   * @return whether it is a String (boxed or unboxed) or a subtype (e.g., a RegEx)
   */
  public static boolean isStringOrSubType(SymTypeExpression type) {
    return getDelegate()._isStringOrSubType(type);
  }

  protected abstract boolean _isStringOrSubType(SymTypeExpression type);

  // Top, Bottom

  public static boolean isTop(SymTypeExpression type) {
    return getDelegate()._isTop(type);
  }

  protected abstract boolean _isTop(SymTypeExpression type);

  public static boolean isBottom(SymTypeExpression type) {
    return getDelegate()._isBottom(type);
  }

  protected abstract boolean _isBottom(SymTypeExpression type);

  // Helper, internals

  /**
   * Same as {@link #isCompatible(SymTypeExpression, SymTypeExpression)},
   * but returns the bounds on inference variables.
   */
  public static List<Bound> constrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    return getDelegate()._constrainCompatible(target, source);
  }

  protected abstract List<Bound> _constrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  );

  /**
   * Same as {@link #isSubTypeOf(SymTypeExpression, SymTypeExpression)},
   * but returns the bounds on inference variables.
   */
  public static List<Bound> constrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return getDelegate()._constrainSubTypeOf(subType, superType);
  }

  protected abstract List<Bound> _constrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  );

  /**
   * Same as {@link #constrainSubTypeOf(SymTypeExpression, SymTypeExpression)},
   * but the arguments are expected to have been normalized
   * (see {@link #normalize(SymTypeExpression)}).
   * This is required to not create infinite loops during normalization.
   */
  public static List<Bound> internal_constrainSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return getDelegate()._internal_constrainSubTypeOfPreNormalized(subType, superType);
  }

  protected abstract List<Bound> _internal_constrainSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType
  );

  /**
   * returns the list of Bounds on the free type variables,
   * if the inputs are to be the same type.
   * Due to union/intersection types,
   * this cannot (trivially/at all?) be replaced with constraining
   * the subtyping relationship in both directions and collecting the bounds.
   */
  public static List<Bound> constrainSameType(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  ) {
    return getDelegate()._constrainSameType(typeA, typeB);
  }

  protected abstract List<Bound> _constrainSameType(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  );

  /**
   * @deprecated use constrain* methods above.
   */
  @Deprecated(forRemoval = true)
  public static boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return isSubTypeOf(subType, superType);
  }

  /**
   * @deprecated Use constrain* methods above.
   */
  @Deprecated(forRemoval = true)
  public static boolean internal_isSubTypeOfPreNormalized(SymTypeExpression subType, SymTypeExpression superType, boolean subTypeIsSoft) {
    return internal_constrainSubTypeOfPreNormalized(subType, superType).isEmpty();
  }

  // static delegate

  public static void init() {
    Log.trace("init default SymTypeRelations", "TypeCheck setup");
    setDelegate(new SymTypeRelationsDefaultDelegatee());
  }

  public static void reset() {
    SymTypeRelations.delegate = null;
  }

  protected static void setDelegate(SymTypeRelations newDelegate) {
    SymTypeRelations.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static SymTypeRelations getDelegate() {
    if (SymTypeRelations.delegate == null) {
      init();
    }
    return SymTypeRelations.delegate;
  }

}

