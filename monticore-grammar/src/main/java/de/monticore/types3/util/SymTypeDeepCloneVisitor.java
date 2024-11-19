// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.*;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * clones SymTypeExpressions
 * its main usage is to be derived from,
 * to generate SymTypeExpressions, which are "not quite" clones,
 * e.g., boxing of SymTypes
 * Usage:
 * calculate(mySymType)
 */
public class SymTypeDeepCloneVisitor implements ISymTypeVisitor {

  protected Stack<SymTypeExpression> transformedSymTypes = new Stack<>();

  protected Stack<SymTypeExpression> getTransformedSymTypes() {
    return transformedSymTypes;
  }

  public void reset() {
    this.transformedSymTypes = new Stack<>();
  }

  /**
   * returns the transformed SymTypeExpression
   */
  public SymTypeExpression getTransformedSymType() {
    if (getTransformedSymTypes().isEmpty()) {
      Log.error("0xFD822 internal error: getting empty result,"
          + " did the visitor not run?");
    }
    if (getTransformedSymTypes().size() > 1) {
      Log.error("0xFD823 internal error: getting result"
          + " while partial results are present");
    }
    return getTransformedSymTypes().peek();
  }

  protected SymTypeExpression popTransformedSubSymType() {
    if (getTransformedSymTypes().isEmpty()) {
      Log.error("0xFD824 internal error: getting partial result"
          + " while partial results are not present");
    }
    return getTransformedSymTypes().pop();
  }

  protected void pushTransformedSymType(SymTypeExpression type) {
    getTransformedSymTypes().push(type);
  }

  @Override
  public void visit(SymTypeArray symType) {
    symType.getArgument().accept(this);
    SymTypeArray clone = SymTypeExpressionFactory
        .createTypeArray(popTransformedSubSymType(), symType.getDim());
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeObscure symType) {
    SymTypeObscure clone = SymTypeExpressionFactory.createObscureType();
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfFunction symType) {
    symType.getType().accept(this);
    FunctionSymbol symbol = null;
    if (symType.hasSymbol()) {
      symbol = symType.getSymbol();
    }
    SymTypeExpression transformedReturnType = popTransformedSubSymType();
    List<SymTypeExpression> transformedArgumentsTypes =
        applyToCollection(symType.getArgumentTypeList());
    SymTypeOfFunction clone = SymTypeExpressionFactory.createFunction(
        symbol,
        transformedReturnType,
        transformedArgumentsTypes,
        symType.isElliptic()
    );
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    List<SymTypeExpression> clonedArguments =
        applyToCollection(symType.getArgumentList());
    SymTypeOfGenerics clone = SymTypeExpressionFactory
        .createGenerics(symType.getTypeInfo(), clonedArguments);
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfIntersection symType) {
    Set<SymTypeExpression> clonedIntersectedTypes =
        applyToCollection(symType.getIntersectedTypeSet());
    SymTypeOfIntersection clone =
        SymTypeExpressionFactory.createIntersection(clonedIntersectedTypes);
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfNull symType) {
    SymTypeOfNull clone = SymTypeExpressionFactory.createTypeOfNull();
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfObject symType) {
    SymTypeOfObject clone =
        SymTypeExpressionFactory.createTypeObject(symType.getTypeInfo());
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfRegEx symType) {
    SymTypeOfRegEx clone =
        SymTypeExpressionFactory.createTypeRegEx((symType.getRegExString()));
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfTuple symType) {
    List<SymTypeExpression> clonedListedTypes =
        applyToCollection(symType.getTypeList());
    SymTypeOfTuple clone =
        SymTypeExpressionFactory.createTuple(clonedListedTypes);
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfUnion symType) {
    Set<SymTypeExpression> clonedUnionizedTypes =
        applyToCollection(symType.getUnionizedTypeSet());
    SymTypeOfUnion clone =
        SymTypeExpressionFactory.createUnion(clonedUnionizedTypes);
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypePrimitive symType) {
    SymTypePrimitive clone =
        SymTypeExpressionFactory.createPrimitive(symType.getTypeInfo());
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    List<SIUnitBasic> numerator = siUnit.getNumerator().stream()
        .map(SIUnitBasic::deepClone)
        .collect(Collectors.toList());
    List<SIUnitBasic> denominator = siUnit.getDenominator().stream()
        .map(SIUnitBasic::deepClone)
        .collect(Collectors.toList());
    SymTypeOfSIUnit clone =
        SymTypeExpressionFactory.createSIUnit(numerator, denominator);
    clone._internal_setSourceInfo(siUnit.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    numericWithSIUnit.getNumericType().accept(this);
    SymTypeExpression numericType = popTransformedSubSymType();
    numericWithSIUnit.getSIUnitType().accept(this);
    SymTypeOfSIUnit siUnitType = (SymTypeOfSIUnit) popTransformedSubSymType();
    SymTypeOfNumericWithSIUnit clone = SymTypeExpressionFactory.
        createNumericWithSIUnit(siUnitType, numericType);
    clone._internal_setSourceInfo(numericWithSIUnit.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeVariable symType) {
    SymTypeVariable clone;
    if (symType.hasTypeVarSymbol()) {
      clone = SymTypeExpressionFactory.createTypeVariable(
          symType.getTypeVarSymbol(),
          symType.getStoredLowerBound(),
          symType.getStoredUpperBound()
      );
    }
    else {
      clone = SymTypeExpressionFactory.createTypeVariable(
          symType.getFreeVarIdentifier(),
          symType.getStoredLowerBound(),
          symType.getStoredUpperBound()
      );
    }
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeVoid symType) {
    SymTypeVoid clone = SymTypeExpressionFactory.createTypeVoid();
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  @Override
  public void visit(SymTypeOfWildcard symType) {
    SymTypeOfWildcard clone;
    if (symType.hasBound()) {
      symType.getBound().accept(this);
      clone = SymTypeExpressionFactory.createWildcard(
          symType.isUpper(), popTransformedSubSymType()
      );
    }
    else {
      clone = SymTypeExpressionFactory.createWildcard();
    }
    clone._internal_setSourceInfo(symType.getSourceInfo());
    pushTransformedSymType(clone);
  }

  // Helpers

  /**
   * uses this visitor with the provided symType and returns the result.
   * it is reset during the process.
   */
  public SymTypeExpression calculate(SymTypeExpression symType) {
    // save stack to allow for recursive calling
    Stack<SymTypeExpression> oldStack = this.transformedSymTypes;
    reset();
    symType.accept(this);
    SymTypeExpression result = getTransformedSymType();
    // restore stack
    this.transformedSymTypes = oldStack;
    return result;
  }

  protected List<SymTypeExpression> applyToCollection(
      List<SymTypeExpression> symTypes) {
    List<SymTypeExpression> transformedTypes = new ArrayList<>();
    for (SymTypeExpression type : symTypes) {
      type.accept(this);
      transformedTypes.add(popTransformedSubSymType());
    }
    return transformedTypes;
  }

  protected Set<SymTypeExpression> applyToCollection(
      Set<SymTypeExpression> symTypes) {
    Set<SymTypeExpression> transformedTypes = new HashSet<>();
    for (SymTypeExpression type : symTypes) {
      type.accept(this);
      transformedTypes.add(popTransformedSubSymType());
    }
    return transformedTypes;
  }

}
