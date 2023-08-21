/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.ISymTypeRelations;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * default implementation
 */
public class SymTypeRelations implements ISymTypeRelations {

  protected SymTypeCompatibilityCalculator compatibilityDelegate;

  protected NominalSuperTypeCalculator superTypeCalculator;

  protected SymTypeBoxingVisitor boxingVisitor;

  protected SymTypeUnboxingVisitor unboxingVisitor;

  protected SymTypeNormalizeVisitor normalizeVisitor;

  protected SymTypeLubCalculator lubDelegate;

  protected BuiltInTypeRelations builtInRelationsDelegate;

  protected FunctionRelations functionRelationsDelegate;

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

  public boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner) {
    return compatibilityDelegate.isCompatible(assignee, assigner);
  }

  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return compatibilityDelegate.isSubTypeOf(subType, superType);
  }

  public List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getNominalSuperTypes(thisType);
  }

  public Optional<SymTypeExpression> leastUpperBound(
      Collection<SymTypeExpression> types) {
    return lubDelegate.leastUpperBound(types);
  }

  public SymTypeExpression box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  public SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  public SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    return builtInRelationsDelegate.numericPromotion(types);
  }

  public boolean isNumericType(SymTypeExpression type) {
    return builtInRelationsDelegate.isNumericType(type);
  }

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
   * @deprecated use {@link FunctionRelations}
   */
  @Deprecated
  public boolean canBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args
  ) {
    return functionRelationsDelegate.canBeCalledWith(func, args);
  }

  /**
   * @deprecated use {@link FunctionRelations}
   */
  @Deprecated
  public Optional<SymTypeOfFunction> getMostSpecificFunction(
      Collection<SymTypeOfFunction> funcs
  ) {
    return functionRelationsDelegate.getMostSpecificFunction(funcs);
  }

  public SymTypeExpression normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

  // Helper, internals

  public boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOf(subType, superType, subTypeIsSoft);
  }

  public boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return compatibilityDelegate
        .internal_isSubTypeOfPreNormalized(subType, superType, subTypeIsSoft);
  }

}

