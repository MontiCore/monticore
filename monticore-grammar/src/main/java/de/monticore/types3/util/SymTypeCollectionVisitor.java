// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.*;
import de.monticore.types3.ISymTypeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Collects contained SymTypeExpressions based on a predicate, e.g.,
 * type: (List<A>, Set<List<C>>) -> void
 * predicate: isList
 * result: List<A>, List<C>
 * Usage:
 * calculate(mySymType, predicate)
 */
public class SymTypeCollectionVisitor implements ISymTypeVisitor {

  protected List<SymTypeExpression> types;

  protected Predicate<SymTypeExpression> filter;

  protected boolean allowDuplicates;

  public void reset() {
    this.types = new ArrayList<>();
    this.filter = x -> false;
    this.allowDuplicates = false;
  }

  protected List<SymTypeExpression> getCollectedTypes() {
    return types;
  }

  protected void addType(SymTypeExpression type) {
    // could be faster per comparator
    if (allowDuplicates ||
        getCollectedTypes().stream().noneMatch(type::deepEquals)) {
      getCollectedTypes().add(type);
    }
  }

  @Override
  public void visit(SymTypeArray symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    symType.getArgument().accept(this);
  }

  @Override
  public void visit(SymTypeObscure obscure) {
    if (filter.test(obscure)) {
      addType(obscure);
    }
  }

  @Override
  public void visit(SymTypeOfFunction symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    symType.getType().accept(this);
    for (SymTypeExpression par : symType.getArgumentTypeList()) {
      par.accept(this);
    }
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    for (SymTypeExpression arg : symType.getArgumentList()) {
      arg.accept(this);
    }
  }

  @Override
  public void visit(SymTypeOfIntersection symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    for (SymTypeExpression arg : symType.getIntersectedTypeSet()) {
      arg.accept(this);
    }
  }

  @Override
  public void visit(SymTypeOfNull nullSymType) {
    if (filter.test(nullSymType)) {
      addType(nullSymType);
    }
  }

  @Override
  public void visit(SymTypeOfObject object) {
    if (filter.test(object)) {
      addType(object);
    }

  }

  @Override
  public void visit(SymTypeOfRegEx regex) {
    if (filter.test(regex)) {
      addType(regex);
    }

  }

  @Override
  public void visit(SymTypeOfTuple symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    for (SymTypeExpression arg : symType.getTypeList()) {
      arg.accept(this);
    }
  }

  @Override
  public void visit(SymTypeOfUnion symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    for (SymTypeExpression arg : symType.getUnionizedTypeSet()) {
      arg.accept(this);
    }
  }

  @Override
  public void visit(SymTypePrimitive primitive) {
    if (filter.test(primitive)) {
      addType(primitive);
    }
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    if (filter.test(siUnit)) {
      addType(siUnit);
    }
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    if (filter.test(numericWithSIUnit)) {
      addType(numericWithSIUnit);
    }
    numericWithSIUnit.getNumericType().accept(this);
    numericWithSIUnit.getSIUnitType().accept(this);
  }

  @Override
  public void visit(SymTypeVariable typeVar) {
    if (filter.test(typeVar)) {
      addType(typeVar);
    }
  }

  @Override
  public void visit(SymTypeVoid voidSymType) {
    if (filter.test(voidSymType)) {
      addType(voidSymType);
    }
  }

  @Override
  public void visit(SymTypeOfWildcard symType) {
    if (filter.test(symType)) {
      addType(symType);
    }
    if (symType.hasBound()) {
      symType.getBound().accept(this);
    }
  }

  // Helpers

  /**
   * uses this visitor with the provided symType and returns the result.
   * it is reset during the process.
   */
  public List<SymTypeExpression> calculate(
      SymTypeExpression symType,
      Predicate<SymTypeExpression> filter,
      boolean allowDuplicates
  ) {
    reset();
    this.filter = filter;
    this.allowDuplicates = allowDuplicates;
    symType.accept(this);
    return getCollectedTypes();
  }

  public List<SymTypeExpression> calculate(
      SymTypeExpression symType,
      Predicate<SymTypeExpression> filter
  ) {
    return calculate(symType, filter, false);
  }

}
