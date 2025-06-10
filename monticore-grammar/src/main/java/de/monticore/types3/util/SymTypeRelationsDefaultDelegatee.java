/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.bounds.Bound;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of SymTypeRelations.
 * <p>
 * As this is a collection of a lot of independent functionality,
 * this itself simply delegates to discrete implementations.
 */
public class SymTypeRelationsDefaultDelegatee extends SymTypeRelations {

  protected SymTypeCompatibilityCalculator compatibilityDelegate;

  protected NominalSuperTypeCalculator superTypeCalculator;

  protected SymTypeBoxingVisitor boxingVisitor;

  protected SymTypeUnboxingVisitor unboxingVisitor;

  protected SymTypeNormalizeVisitor normalizeVisitor;

  protected SymTypeLubCalculator lubDelegate;

  protected BuiltInTypeRelations builtInRelationsDelegate;

  public SymTypeRelationsDefaultDelegatee() {
    // default values
    compatibilityDelegate = new SymTypeCompatibilityCalculator();
    superTypeCalculator = new NominalSuperTypeCalculator();
    boxingVisitor = new SymTypeBoxingVisitor();
    unboxingVisitor = new SymTypeUnboxingVisitor();
    normalizeVisitor = new SymTypeNormalizeVisitor();
    lubDelegate = new SymTypeLubCalculator();
    builtInRelationsDelegate = new BuiltInTypeRelations();
  }

  @Override
  protected boolean _isCompatible(SymTypeExpression target, SymTypeExpression source) {
    return compatibilityDelegate.isCompatible(target, source);
  }

  @Override
  protected boolean _isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return compatibilityDelegate.isSubTypeOf(subType, superType);
  }

  @Override
  protected List<SymTypeExpression> _getNominalSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getNominalSuperTypes(thisType);
  }

  @Override
  protected Optional<SymTypeExpression> _leastUpperBound(Collection<SymTypeExpression> types) {
    return lubDelegate.leastUpperBound(types);
  }

  @Override
  protected SymTypeExpression _box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  @Override
  protected SymTypeExpression _unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  @Override
  protected SymTypeExpression _normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

  // primitives

  @Override
  protected SymTypeExpression _numericPromotion(List<SymTypeExpression> types) {
    return builtInRelationsDelegate.numericPromotion(types);
  }

  @Override
  protected boolean _isNumericType(SymTypeExpression type) {
    return builtInRelationsDelegate.isNumericType(type);
  }

  @Override
  protected boolean _isIntegralType(SymTypeExpression type) {
    return builtInRelationsDelegate.isIntegralType(type);
  }

  @Override
  protected boolean _isBoolean(SymTypeExpression type) {
    return builtInRelationsDelegate.isBoolean(type);
  }

  @Override
  protected boolean _isInt(SymTypeExpression type) {
    return builtInRelationsDelegate.isInt(type);
  }

  @Override
  protected boolean _isDouble(SymTypeExpression type) {
    return builtInRelationsDelegate.isDouble(type);
  }

  @Override
  protected boolean _isFloat(SymTypeExpression type) {
    return builtInRelationsDelegate.isFloat(type);
  }

  @Override
  protected boolean _isLong(SymTypeExpression type) {
    return builtInRelationsDelegate.isLong(type);
  }

  @Override
  protected boolean _isChar(SymTypeExpression type) {
    return builtInRelationsDelegate.isChar(type);
  }

  @Override
  protected boolean _isShort(SymTypeExpression type) {
    return builtInRelationsDelegate.isShort(type);
  }

  @Override
  protected boolean _isByte(SymTypeExpression type) {
    return builtInRelationsDelegate.isByte(type);
  }

  @Override
  protected boolean _isString(SymTypeExpression type) {
    return builtInRelationsDelegate.isString(type);
  }

  @Override
  protected boolean _isStringOrSubType(SymTypeExpression type) {
    return builtInRelationsDelegate.isStringOrSubType(type);
  }

  // Top, Bottom

  @Override
  protected boolean _isTop(SymTypeExpression type) {
    return builtInRelationsDelegate.isTop(type);
  }

  @Override
  protected boolean _isBottom(SymTypeExpression type) {
    return builtInRelationsDelegate.isBottom(type);
  }

  // Helper, internals

  @Override
  protected List<Bound> _constrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    return compatibilityDelegate.constrainCompatible(target, source);
  }

  @Override
  protected List<Bound> _constrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return compatibilityDelegate.constrainSubTypeOf(subType, superType);
  }

  @Override
  protected List<Bound> _internal_constrainSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return compatibilityDelegate.internal_constrainSubTypeOfPreNormalized(
        subType, superType
    );
  }

  @Override
  protected List<Bound> _constrainSameType(
      SymTypeExpression typeA,
      SymTypeExpression typeB) {
    return compatibilityDelegate.constrainSameType(typeA, typeB);
  }

}
