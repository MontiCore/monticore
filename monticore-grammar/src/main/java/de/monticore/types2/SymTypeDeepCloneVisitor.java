// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
    getTransformedSymTypes().clear();
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
    if (getTransformedSymTypes().size() < 1) {
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
    pushTransformedSymType(SymTypeExpressionFactory
        .createTypeArray(popTransformedSubSymType(), symType.getDim()));
  }

  @Override
  public void visit(SymTypeObscure symType) {
    Log.warn("0xFD825 internal warning: transforming obscure type");
    pushTransformedSymType(SymTypeExpressionFactory.createObscureType());
  }

  @Override
  public void visit(SymTypeOfFunction symType) {
    symType.getType().accept(this);
    SymTypeExpression transformedReturnType = popTransformedSubSymType();
    List<SymTypeExpression> transformedArgumentsTypes =
        applyToCollection(symType.getArgumentTypeList());
    pushTransformedSymType(SymTypeExpressionFactory.createFunction(
        transformedReturnType, transformedArgumentsTypes, symType.isElliptic()
    ));
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    List<SymTypeExpression> clonedArguments = new LinkedList<>();
    for (SymTypeExpression argument : symType.getArgumentList()) {
      clonedArguments.add(argument.deepClone());
    }
    pushTransformedSymType(
        SymTypeExpressionFactory.createGenerics(symType.getTypeInfo(), clonedArguments)
    );
  }

  @Override
  public void visit(SymTypeOfIntersection symType) {
    Set<SymTypeExpression> clonedIntersectedTypes =
        applyToCollection(symType.getIntersectedTypeSet());
    pushTransformedSymType(
        SymTypeExpressionFactory.createIntersection(clonedIntersectedTypes)
    );
  }

  @Override
  public void visit(SymTypeOfNull symType) {
    pushTransformedSymType(SymTypeExpressionFactory.createTypeOfNull());
  }

  @Override
  public void visit(SymTypeOfObject symType) {
    pushTransformedSymType(
        SymTypeExpressionFactory.createTypeObject(symType.getTypeInfo())
    );
  }

  @Override
  public void visit(SymTypeOfUnion symType) {
    Set<SymTypeExpression> clonedUnionizedTypes =
        applyToCollection(symType.getUnionizedTypeSet());
    pushTransformedSymType(
        SymTypeExpressionFactory.createUnion(clonedUnionizedTypes)
    );
  }

  @Override
  public void visit(SymTypePrimitive symType) {
    pushTransformedSymType(
        SymTypeExpressionFactory.createPrimitive(symType.getTypeInfo())
    );
  }

  @Override
  public void visit(SymTypeVariable symType) {
    pushTransformedSymType(
        SymTypeExpressionFactory.createTypeVariable(symType.getTypeVarSymbol())
    );
  }

  @Override
  public void visit(SymTypeVoid symType) {
    pushTransformedSymType(SymTypeExpressionFactory.createTypeVoid());
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
    pushTransformedSymType(clone);
  }

  // Helpers

  /**
   * uses this visitor with the provided symType and returns the result
   */
  public SymTypeExpression calculate(SymTypeExpression symType) {
    reset();
    symType.accept(this);
    return getTransformedSymType();
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
