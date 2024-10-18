/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.util.BuiltInTypeRelations;
import de.monticore.types3.util.NominalSuperTypeCalculator;
import de.monticore.types3.util.SymTypeBoxingVisitor;
import de.monticore.types3.util.SymTypeCompatibilityCalculator;
import de.monticore.types3.util.SymTypeLubCalculator;
import de.monticore.types3.util.SymTypeNormalizeVisitor;
import de.monticore.types3.util.SymTypeUnboxingVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Relations of SymTypeExpressions
 * <p>
 * some are dependent on the specific type system
 * and as such not hardcoded in the SymTypeExpressions themselves
 */
public class SymTypeRelations {

  protected static SymTypeCompatibilityCalculator compatibilityDelegate;

  protected static NominalSuperTypeCalculator superTypeCalculator;

  protected static SymTypeBoxingVisitor boxingVisitor;

  protected static SymTypeUnboxingVisitor unboxingVisitor;

  protected static SymTypeNormalizeVisitor normalizeVisitor;

  protected static SymTypeLubCalculator lubDelegate;

  protected static BuiltInTypeRelations builtInRelationsDelegate;

  public static void init() {
    Log.trace("init default SymTypeRelations", "TypeCheck setup");
    // default values
    compatibilityDelegate = new SymTypeCompatibilityCalculator();
    superTypeCalculator = new NominalSuperTypeCalculator();
    boxingVisitor = new SymTypeBoxingVisitor();
    unboxingVisitor = new SymTypeUnboxingVisitor();
    normalizeVisitor = new SymTypeNormalizeVisitor();
    lubDelegate = new SymTypeLubCalculator();
    builtInRelationsDelegate = new BuiltInTypeRelations();
  }

  static {
    init();
  }

  /**
   * whether the target can be assigned to by the source,
   * e.g., assignment operator: x = 2,
   * -> type of x and type of 2 need to be compatible,
   * e.g., functions call: (float -> void)(2),
   * -> float and type of 2 need to be compatible
   */
  public static boolean isCompatible(SymTypeExpression target, SymTypeExpression source) {
    return compatibilityDelegate.isCompatible(target, source);
  }

  /**
   * whether subType is the sub-type of superType,
   * Examples:
   * isSubType(Person, Person)
   * isSubType(Student, Person)
   * !isSubType(Person, Student)
   * isSubType(int, float)
   * !isSubType(float, int)
   */
  public static boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return compatibilityDelegate.isSubTypeOf(subType, superType);
  }

  /**
   * returns nominal supertypes.
   * Nominal supertypes are those that are explicitly listed as super types,
   * e.g., in Java those specified using "extends" or "implements".
   * The return value is neither the reflexive nor the transitive closure,
   * i.e., only the direct supertypes are included (s. Java spec 20 4.10).
   * Note that the "direct" supertype-relation is deliberately underspecified,
   * such that it can be refined according to the specific type system's needs.
   */
  public static List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getNominalSuperTypes(thisType);
  }

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
   * https://git.rwth-aachen.de/monticore/monticore/-/issues/4187
   * empty represents the universal type (aka the lack of a bound)
   * Obscure is returned, if no lub could be calculated, e.g. lub(int, Person)
   */
  public static Optional<SymTypeExpression> leastUpperBound(Collection<SymTypeExpression> types) {
    return lubDelegate.leastUpperBound(types);
  }

  public static Optional<SymTypeExpression> leastUpperBound(SymTypeExpression... types) {
    return leastUpperBound(List.of(types));
  }

  /**
   * Boxes SymTypeExpressions,
   * including, but not limited to, Java primitive boxing
   * e.g., int -> java.lang.Integer
   * e.g., List -> java.util.List
   */
  public static SymTypeExpression box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  /**
   * Unboxes SymTypeExpressions,
   * including, but not limited to, Java primitive unboxing
   * e.g., java.lang.Integer -> int
   * e.g., java.util.List -> List
   */
  public static SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  /**
   * normalizes the SymTypeExpression,
   * e.g., (A & B[])[] -> (A[] & B[][])
   * <p>
   * Within our type systems, each type has ONE normalized representation.
   * This can be used to, e.g., compare SymTypeExpressions
   */
  public static SymTypeExpression normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

  // primitives

  /**
   * calculates the one promoted numeric type,
   * ignoring the specifics of the context
   * s. Java spec. 20 5.6
   * e.g., short -> int
   * e.g., byte, float -> float
   */
  public static SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    return builtInRelationsDelegate.numericPromotion(types);
  }

  public static SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  /**
   * test if the expression is of numeric type,
   * e.g., in Java: (double, float, long, int, char, short, byte)
   */
  public static boolean isNumericType(SymTypeExpression type) {
    return builtInRelationsDelegate.isNumericType(type);
  }

  /**
   * test if the expression is of integral type,
   * e.g., in Java: (long, int, char, short, byte)
   */
  public static boolean isIntegralType(SymTypeExpression type) {
    return builtInRelationsDelegate.isIntegralType(type);
  }

  public static boolean isBoolean(SymTypeExpression type) {
    return builtInRelationsDelegate.isBoolean(type);
  }

  public static boolean isInt(SymTypeExpression type) {
    return builtInRelationsDelegate.isInt(type);
  }

  public static boolean isDouble(SymTypeExpression type) {
    return builtInRelationsDelegate.isDouble(type);
  }

  public static boolean isFloat(SymTypeExpression type) {
    return builtInRelationsDelegate.isFloat(type);
  }

  public static boolean isLong(SymTypeExpression type) {
    return builtInRelationsDelegate.isLong(type);
  }

  public static boolean isChar(SymTypeExpression type) {
    return builtInRelationsDelegate.isChar(type);
  }

  public static boolean isShort(SymTypeExpression type) {
    return builtInRelationsDelegate.isShort(type);
  }

  public static boolean isByte(SymTypeExpression type) {
    return builtInRelationsDelegate.isByte(type);
  }

  public static boolean isString(SymTypeExpression type) {
    return builtInRelationsDelegate.isString(type);
  }

  // Top, Bottom

  public static boolean isTop(SymTypeExpression type) {
    return builtInRelationsDelegate.isTop(type);
  }

  public static boolean isBottom(SymTypeExpression type) {
    return builtInRelationsDelegate.isBottom(type);
  }

  // Helper, internals

  /**
   * Same as {@link #isCompatible(SymTypeExpression, SymTypeExpression)},
   * but returns the bounds on inference variables.
   */
  public static List<Bound> constrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    return compatibilityDelegate.constrainCompatible(target, source);
  }

  /**
   * Same as {@link #isSubTypeOf(SymTypeExpression, SymTypeExpression)},
   * but returns the bounds on inference variables.
   */
  public static List<Bound> constrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return compatibilityDelegate.constrainSubTypeOf(subType, superType);
  }

  /**
   * same as {@link #constrainSubTypeOf(SymTypeExpression, SymTypeExpression)},
   * but the arguments are expected to have been normalized
   * (s. {@link #normalize(SymTypeExpression)}).
   * This is required to not create infinite loops during normalization.
   */
  public static List<Bound> internal_constrainSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return compatibilityDelegate.internal_constrainSubTypeOfPreNormalized(
        subType, superType
    );
  }

  /**
   * returns the list of Bounds on the free type variables,
   * if the inputs are to be the same type.
   * Due to union/intersection types,
   * this cannot (trivially/at all?) be replaced with constraining
   * the subtyping relationship in both directions and collection the bounds.
   */
  public static List<Bound> constrainSameType(
      SymTypeExpression typeA,
      SymTypeExpression typeB) {
    return compatibilityDelegate.constrainSameType(typeA, typeB);
  }

  /**
   * @deprecated use constrain* methods above
   */
  @Deprecated
  public static boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOf(subType, superType, subTypeIsSoft);
  }

  /**
   * @deprecated use constrain* methods above
   */
  @Deprecated
  public static boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOfPreNormalized(subType, superType, subTypeIsSoft);
  }

}

