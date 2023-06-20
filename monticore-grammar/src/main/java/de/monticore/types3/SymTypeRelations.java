/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.BuiltInTypeRelations;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.SymTypeBoxer;
import de.monticore.types3.util.SymTypeCompatibilityCalculator;
import de.monticore.types3.util.SymTypeLubCalculator;
import de.monticore.types3.util.SymTypeNormalizer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Relations of SymTypeExpressions
 * <p>
 * some are dependent on the specific type system
 * and as such not hardcoded in the SymTypeExpressions themselves
 * <p>
 * uses the realThis pattern and as such needs to be initialized before use:
 * {@link SymTypeRelations#initDefault()}
 */
public class SymTypeRelations {

  // for realThis pattern
  // the pattern from doi:10.5381/jot.2022.21.4.a4 has been slightly modified
  // to be less flexible
  // (calls from a delegate method to a method implemented
  // in the same delegate cannot be intercepted by the realThis object),
  // but also less prone to errors due to negligence
  // (forgetting to call getRealThis in the delegate)

  protected SymTypeRelations symTypeRelations;

  protected SymTypeRelations getSymTypeRelations() {
    return symTypeRelations;
  }

  protected void setSymTypeRelations(SymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
  }

  // realThis delegates

  SymTypeRelations compatibilityDelegate;

  SymTypeRelations boxingDelegate;

  SymTypeRelations normalizeDelegate;

  SymTypeRelations lubDelegate;

  SymTypeRelations buildInRelationsDelegate;

  SymTypeRelations functionRelationsDelegate;

  // end realThis pattern

  public void initDefault() {
    // default values
    this.symTypeRelations = this;
    this.compatibilityDelegate = new SymTypeCompatibilityCalculator();
    this.compatibilityDelegate.setSymTypeRelations(this);
    this.boxingDelegate = new SymTypeBoxer();
    this.boxingDelegate.setSymTypeRelations(this);
    this.normalizeDelegate = new SymTypeNormalizer();
    this.normalizeDelegate.setSymTypeRelations(this);
    this.lubDelegate = new SymTypeLubCalculator();
    this.lubDelegate.setSymTypeRelations(this);
    this.buildInRelationsDelegate = new BuiltInTypeRelations();
    this.buildInRelationsDelegate.setSymTypeRelations(this);
    this.functionRelationsDelegate = new FunctionRelations();
    this.functionRelationsDelegate.setSymTypeRelations(this);
  }

  /**
   * whether the assignee can be assigned to by the assigner,
   * e.g., assignment operator: x = 2,
   * -> type of x and type of 2 need to be compatible,
   * e.g., functions call: (float -> void)(2),
   * -> float and type of 2 need to be compatible
   */
  public boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner) {
    return getSymTypeRelations().compatibilityDelegate
        .isCompatible(assignee, assigner);
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
    return getSymTypeRelations().compatibilityDelegate
        .isSubTypeOf(subType, superType);
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
    return getSymTypeRelations().lubDelegate.leastUpperBound(types);
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
    return getSymTypeRelations().boxingDelegate.box(unboxed);
  }

  /**
   * Unboxes SymTypeExpressions,
   * including, but not limited to, Java primitive unboxing
   * e.g., java.lang.Integer -> int
   * e.g., java.util.List -> List
   */
  public SymTypeExpression unbox(SymTypeExpression boxed) {
    return getSymTypeRelations().boxingDelegate.unbox(boxed);
  }

  /**
   * calculates the one promoted numeric type,
   * ignoring the specifics of the context
   * s. Java spec. 20 5.6
   * e.g., short -> int
   * e.g., byte, float -> float
   */
  public SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    return getSymTypeRelations().buildInRelationsDelegate
        .numericPromotion(types);
  }

  public SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  /**
   * test if the expression is of numeric type
   * (double, float, long, int, char, short, byte)
   */
  public boolean isNumericType(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isNumericType(type);
  }

  /**
   * test if the expression is of integral type
   * (long, int, char, short, byte)
   */
  public boolean isIntegralType(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isIntegralType(type);
  }

  public boolean isBoolean(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isBoolean(type);
  }

  public boolean isInt(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isInt(type);
  }

  public boolean isDouble(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isDouble(type);
  }

  public boolean isFloat(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isFloat(type);
  }

  public boolean isLong(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isLong(type);
  }

  public boolean isChar(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isChar(type);
  }

  public boolean isShort(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isShort(type);
  }

  public boolean isByte(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isByte(type);
  }

  public boolean isString(SymTypeExpression type) {
    return getSymTypeRelations().buildInRelationsDelegate
        .isString(type);
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
    return getSymTypeRelations().functionRelationsDelegate
        .canBeCalledWith(func, args);
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
    return getSymTypeRelations().functionRelationsDelegate
        .getMostSpecificFunction(funcs);
  }

  // Helper, internals

  /**
   * normalizes the SymTypeExpression,
   * e.g., (A & B[])[] -> (A[] & B[][])
   *
   * Within our type systems, each type has ONE normalized representation.
   * This can be used to, e.g., compare SymTypeExpressions
   */
  protected SymTypeExpression normalize(SymTypeExpression type) {
    return getSymTypeRelations().normalizeDelegate.normalize(type);
  }

  /**
   * internal isSubTypeOf,
   * subTypeIsSoft if it is only a possibility, that it is a subtype.
   * a type can be a subtype of a variable. but it is not required to be
   */
  protected boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return getSymTypeRelations().compatibilityDelegate
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
    return getSymTypeRelations().compatibilityDelegate
        .internal_isSubTypeOfPreNormalized(subType, superType, subTypeIsSoft);
  }

}

