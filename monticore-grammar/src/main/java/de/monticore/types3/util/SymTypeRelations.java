/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SymTypeRelations {

  protected static SymTypeCompatibilityCalculator compatibilityDelegate;

  protected static NominalSuperTypeCalculator superTypeCalculator;

  protected static SymTypeBoxingVisitor boxingVisitor;

  protected static SymTypeUnboxingVisitor unboxingVisitor;

  protected static SymTypeNormalizeVisitor normalizeVisitor;

  protected static SymTypeLubCalculator lubDelegate;

  protected static BuiltInTypeRelations builtInRelationsDelegate;

  public static void init() {
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

  public static boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner) {
    return compatibilityDelegate.isCompatible(assignee, assigner);
  }

  public static boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return compatibilityDelegate.isSubTypeOf(subType, superType);
  }

  public static List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getNominalSuperTypes(thisType);
  }

  public static Optional<SymTypeExpression> leastUpperBound(Collection<SymTypeExpression> types) {
    return lubDelegate.leastUpperBound(types);
  }

  public static Optional<SymTypeExpression> leastUpperBound(SymTypeExpression... types) {
    return leastUpperBound(List.of(types));
  }

  public static SymTypeExpression box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  public static SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  public static SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    return builtInRelationsDelegate.numericPromotion(types);
  }

  public static SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  public static boolean isNumericType(SymTypeExpression type) {
    return builtInRelationsDelegate.isNumericType(type);
  }

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

  public static SymTypeExpression normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

  // Helper, internals

  public static boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOf(subType, superType, subTypeIsSoft);
  }

  public static boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOfPreNormalized(subType, superType, subTypeIsSoft);
  }

}

