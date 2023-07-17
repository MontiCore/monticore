/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.BuiltInTypeRelations;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.NominalSuperTypeCalculator;
import de.monticore.types3.util.SymTypeBoxingVisitor;
import de.monticore.types3.util.SymTypeCompatibilityCalculator;
import de.monticore.types3.util.SymTypeLubCalculator;
import de.monticore.types3.util.SymTypeNormalizeVisitor;
import de.monticore.types3.util.SymTypeUnboxingVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Relations of SymTypeExpressions
 * <p>
 * some are dependent on the specific type system
 * and as such not hardcoded in the SymTypeExpressions themselves
 * <p>
 */
public class SymTypeRelations {

  SymTypeCompatibilityCalculator compatibilityDelegate;

  NominalSuperTypeCalculator superTypeCalculator;

  SymTypeBoxingVisitor boxingVisitor;

  SymTypeUnboxingVisitor unboxingVisitor;

  SymTypeNormalizeVisitor normalizeVisitor;

  SymTypeLubCalculator lubDelegate;

  BuiltInTypeRelations builtInRelationsDelegate;

  FunctionRelations functionRelationsDelegate;

  public SymTypeRelations() {
    // default values
    this.compatibilityDelegate = new SymTypeCompatibilityCalculator(this);
    this.superTypeCalculator = new NominalSuperTypeCalculator(this);
    this.boxingVisitor = new SymTypeBoxingVisitor();
    this.unboxingVisitor = new SymTypeUnboxingVisitor();
    this.normalizeVisitor = new SymTypeNormalizeVisitor(this);
    this.lubDelegate = new SymTypeLubCalculator(this);
    this.builtInRelationsDelegate = new BuiltInTypeRelations();
    this.functionRelationsDelegate = new FunctionRelations(this);
  }

  /**
   * whether the assignee can be assigned to by the assigner,
   * e.g., assignment operator: x = 2,
   * -> type of x and type of 2 need to be compatible,
   * e.g., functions call: (float -> void)(2),
   * -> float and type of 2 need to be compatible
   */
  public boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner) {
    return compatibilityDelegate.isCompatible(assignee, assigner);
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
  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
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
  public List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getNominalSuperTypes(thisType);
  }

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
  public Optional<SymTypeExpression> leastUpperBound(
      Collection<SymTypeExpression> types) {
    return lubDelegate.leastUpperBound(types);
  }

  public Optional<SymTypeExpression> leastUpperBound(
      SymTypeExpression... types) {
    return leastUpperBound(List.of(types));
  }

  /**
   * Boxes SymTypeExpressions,
   * including, but not limited to, Java primitive boxing
   * e.g., int -> java.lang.Integer
   * e.g., List -> java.util.List
   */
  public SymTypeExpression box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  /**
   * Unboxes SymTypeExpressions,
   * including, but not limited to, Java primitive unboxing
   * e.g., java.lang.Integer -> int
   * e.g., java.util.List -> List
   */
  public SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  /**
   * calculates the one promoted numeric type,
   * ignoring the specifics of the context
   * s. Java spec. 20 5.6
   * e.g., short -> int
   * e.g., byte, float -> float
   */
  public SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    return builtInRelationsDelegate.numericPromotion(types);
  }

  public SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  /**
   * test if the expression is of numeric type
   * (double, float, long, int, char, short, byte)
   */
  public boolean isNumericType(SymTypeExpression type) {
    return builtInRelationsDelegate.isNumericType(type);
  }

  /**
   * test if the expression is of integral type
   * (long, int, char, short, byte)
   */
  public boolean isIntegralType(SymTypeExpression type) {
    return builtInRelationsDelegate.isIntegralType(type);
  }

  public boolean isBoolean(SymTypeExpression type) {
    return builtInRelationsDelegate.isBoolean(type);
  }

  public boolean isInt(SymTypeExpression type) {
    return builtInRelationsDelegate.isInt(type);
  }

  public boolean isDouble(SymTypeExpression type) {
    return builtInRelationsDelegate.isDouble(type);
  }

  public boolean isFloat(SymTypeExpression type) {
    return builtInRelationsDelegate.isFloat(type);
  }

  public boolean isLong(SymTypeExpression type) {
    return builtInRelationsDelegate.isLong(type);
  }

  public boolean isChar(SymTypeExpression type) {
    return builtInRelationsDelegate.isChar(type);
  }

  public boolean isShort(SymTypeExpression type) {
    return builtInRelationsDelegate.isShort(type);
  }

  public boolean isByte(SymTypeExpression type) {
    return builtInRelationsDelegate.isByte(type);
  }

  public boolean isString(SymTypeExpression type) {
    return builtInRelationsDelegate.isString(type);
  }

  /**
   * whether the function can be called given specified arguments,
   * this is type system dependent and as such not part of SymTypeOfFunction.
   * Examples:
   * canBeCalledWith(float -> void, [2])
   * canBeCalledWith(int... -> void, [1, 2, 4])
   * !canBeCalledWith(int, float -> void, [2])
   */
  public boolean canBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args
  ) {
    return functionRelationsDelegate.canBeCalledWith(func, args);
  }

  /**
   * for callable overloaded functions, selects the best fitting one.
   * s. Java spec 20 15.12.2.5
   * we expect inferred arities (no elliptic functions),
   * and all have to be callable with the same tuple of arguments.
   */
  public Optional<SymTypeOfFunction> getMostSpecificFunction(
      Collection<SymTypeOfFunction> funcs
  ) {
    return functionRelationsDelegate.getMostSpecificFunction(funcs);
  }

  /**
   * normalizes the SymTypeExpression,
   * e.g., (A & B[])[] -> (A[] & B[][])
   *
   * Within our type systems, each type has ONE normalized representation.
   * This can be used to, e.g., compare SymTypeExpressions
   */
  public SymTypeExpression normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

  // Helper, internals

  /**
   * internal isSubTypeOf,
   * subTypeIsSoft if it is only a possibility, that it is a subtype.
   * a type can be a subtype of a variable. but it is not required to be
   */
  public boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOf(subType, superType, subTypeIsSoft);
  }

  /**
   * internal isSubTypeOf,
   * the types are expected to be normalized,
   * necessary to avoid loops
   */
  public boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOfPreNormalized(subType, superType, subTypeIsSoft);
  }

}

