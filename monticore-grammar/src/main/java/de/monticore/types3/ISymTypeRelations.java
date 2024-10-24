/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Relations of SymTypeExpressions
 * <p>
 * some are dependent on the specific type system
 * and as such not hardcoded in the SymTypeExpressions themselves
 * @deprecated use SymTypeRelations
 */
@Deprecated
public interface ISymTypeRelations {

  /**
   * whether the assignee can be assigned to by the assigner,
   * e.g., assignment operator: x = 2,
   * -> type of x and type of 2 need to be compatible,
   * e.g., functions call: (float -> void)(2),
   * -> float and type of 2 need to be compatible
   */
  boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner);

  /**
   * whether subType is the sub-type of superType,
   * Examples:
   * isSubType(Person, Person)
   * isSubType(Student, Person)
   * !isSubType(Person, Student)
   * isSubType(int, float)
   * !isSubType(float, int)
   */
  boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType);

  /**
   * returns nominal supertypes.
   * Nominal supertypes are those that are explicitly listed as super types,
   * e.g., in Java those specified using "extends" or "implements".
   * The return value is neither the reflexive nor the transitive closure,
   * i.e., only the direct supertypes are included (s. Java spec 20 4.10).
   * Note that the "direct" supertype-relation is deliberately underspecified,
   * such that it can be refined according to the specific type system's needs.
   */
  List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType);

  /**
   * Boxes SymTypeExpressions,
   * including, but not limited to, Java primitive boxing
   * e.g., int -> java.lang.Integer
   * e.g., List -> java.util.List
   */
  SymTypeExpression box(SymTypeExpression unboxed);

  /**
   * Unboxes SymTypeExpressions,
   * including, but not limited to, Java primitive unboxing
   * e.g., java.lang.Integer -> int
   * e.g., java.util.List -> List
   */
  SymTypeExpression unbox(SymTypeExpression boxed);

  /**
   * normalizes the SymTypeExpression,
   * e.g., (A & B[])[] -> (A[] & B[][])
   * <p>
   * Within our type systems, each type has ONE normalized representation.
   * This can be used to, e.g., compare SymTypeExpressions
   */
  SymTypeExpression normalize(SymTypeExpression type);

  /**
   * least upper bound for a set of types
   * for e.g. union types
   * unlike the Java counterpart,
   * we specify it for non-reference types as well,
   * making it more akin to Java conditional expressions,
   * where "a?b:c" has type leastUpperBound(b,c)
   * <p>
   * empty represents the universal type (aka the lack of a bound)
   * Obscure is returned, if no lub could be calculated, e.g. lub(int, Person)
   */
  Optional<SymTypeExpression> leastUpperBound(
      Collection<SymTypeExpression> types);

  default Optional<SymTypeExpression> leastUpperBound(
      SymTypeExpression... types) {
    return leastUpperBound(List.of(types));
  }

  // primitives

  /**
   * calculates the one promoted numeric type,
   * ignoring the specifics of the context
   * s. Java spec. 20 5.6
   * e.g., short -> int
   * e.g., byte, float -> float
   */
  SymTypeExpression numericPromotion(List<SymTypeExpression> types);

  default SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  /**
   * test if the expression is of numeric type,
   * e.g., in Java: (double, float, long, int, char, short, byte)
   */
  boolean isNumericType(SymTypeExpression type);

  /**
   * test if the expression is of integral type,
   * e.g., in Java: (long, int, char, short, byte)
   */
  boolean isIntegralType(SymTypeExpression type);

  boolean isBoolean(SymTypeExpression type);

  boolean isInt(SymTypeExpression type);

  boolean isDouble(SymTypeExpression type);

  boolean isFloat(SymTypeExpression type);

  boolean isLong(SymTypeExpression type);

  boolean isChar(SymTypeExpression type);

  boolean isShort(SymTypeExpression type);

  boolean isByte(SymTypeExpression type);

  boolean isString(SymTypeExpression type);

}

