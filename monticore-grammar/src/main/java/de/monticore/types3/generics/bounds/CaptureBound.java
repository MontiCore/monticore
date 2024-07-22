// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.util.SymTypeExpressionComparator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A bound representing that a given type will be capture converted later.
 * This is required, as one cannot capture convert types
 * that have inference variables.
 * The only SymType-kinds applicable are generics and functions,
 * as their symbols can have type parameters.
 */
public class CaptureBound extends Bound {

  protected SymTypeExpression placeHolder;
  protected SymTypeExpression toBeCaptured;

  public CaptureBound(
      SymTypeExpression toBeCaptured
  ) {
    Log.errorIfNull(toBeCaptured);
    if (!toBeCaptured.isGenericType() && !toBeCaptured.isFunctionType()) {
      Log.error("0xFD229 internal error: " +
          "tried to create CaptureBound with unsupported type "
          + toBeCaptured.printFullName()
      );
    }
    this.toBeCaptured = toBeCaptured;
    this.placeHolder = calculatePlaceHolder(toBeCaptured);
  }

  public SymTypeExpression getPlaceHolder() {
    return placeHolder;
  }

  public SymTypeExpression getToBeCaptured() {
    return toBeCaptured;
  }

  @Override
  public boolean isCaptureBound() {
    return true;
  }

  @Override
  public boolean deepEquals(Bound other) {
    if (this == other) {
      return true;
    }
    if (!other.isCaptureBound()) {
      return false;
    }
    CaptureBound cap = (CaptureBound) other;
    return getPlaceHolder().deepEquals(cap.getPlaceHolder()) &&
        getToBeCaptured().deepEquals(cap.getToBeCaptured());
  }

  @Override
  public String print() {
    return placeHolder.printFullName()
        + " = capture(" + toBeCaptured.printFullName() + ")";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getPlaceHolder(), getToBeCaptured());
  }

  /**
   * returns the inference variables created for this bound,
   * e.g., for bound G<a1,a2> = capture(G<A1,A2>), this will return [a1,a2].
   * s. Java Spec 21 5.1.10.
   * They include the implied bounds according to Java Spec 21 18.1.3.
   */
  public List<SymTypeVariable> getInferenceVariables() {
    List<SymTypeVariable> infVars = getTypeArguments(placeHolder).stream()
        .map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    return infVars;
  }

  /**
   * returns the type arguments to be captured for this bound,
   * e.g., for bound G<a1,a2> = capture(G<A1,A2>), this will return [A1,A2].
   */
  public List<SymTypeExpression> getTypeArguments() {
    return getTypeArguments(toBeCaptured);
  }

  /**
   * returns the type parameters of the declared type
   * e.g., for bound G<a1,a2> = capture(G<A1,A2>) with G declared with
   * type parameters [P1,P2], this will return [P1,P2].
   */
  public List<SymTypeVariable> getTypeParameters() {
    SymTypeExpression declType = getDeclaredType();
    List<SymTypeVariable> typeParams = getTypeArguments(declType).stream()
        .map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    return typeParams;
  }

  /**
   * returns the declared bounds with the type parameters
   * replaced by the inference variables,
   * e.g., for bound G<a1,a2> = capture(G<A1,A2>) with G declared with
   * type parameters [P1,P2] and corresponding (upper) bounds [B1,B2],
   * this will return [B1,B2][P1:=a1,P2:=a2].
   * S. Java Spec 21 18.3.2
   */
  public List<SymTypeExpression> getModifiedDeclaredBounds() {
    List<SymTypeVariable> params = getTypeParameters();
    List<SymTypeExpression> upperBounds = params.stream()
        .map(SymTypeVariable::getUpperBound)
        .collect(Collectors.toList());
    List<SymTypeExpression> modifiedBounds = upperBounds.stream()
        .map(t -> TypeParameterRelations.replaceTypeVariables(
            t, getTypeParameter2InferenceVarMap()
        ))
        .collect(Collectors.toList());
    return modifiedBounds;
  }

  /**
   * returns the mapping from type parameters to inference variables
   * e.g., for bound G<a1,a2> = capture(G<A1,A2>) with G declared with
   * type parameters [P1,P2], this will return [P1:=a1,P2:=a2].
   * S. Java Spec 21 18.3.2
   */
  public Map<SymTypeVariable, SymTypeVariable> getTypeParameter2InferenceVarMap() {
    List<SymTypeVariable> infVars = getInferenceVariables();
    SymTypeExpression declType = toBeCaptured.isGenericType() ?
        toBeCaptured.asGenericType().getDeclaredType() :
        toBeCaptured.asFunctionType().getDeclaredType();
    List<SymTypeVariable> typeParams = getTypeArguments(declType).stream()
        .map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    Map<SymTypeVariable, SymTypeVariable> param2InfVar =
        new TreeMap<>(new SymTypeExpressionComparator());
    for (int i = 0; i < typeParams.size(); i++) {
      param2InfVar.put(typeParams.get(i), infVars.get(i));
    }
    return param2InfVar;
  }

  /**
   * s. Java Spec 21 18.3.2
   * returns the bounds implied by the type arguments that are not wildcards.
   */
  public List<Bound> getImpliedBounds() {
    List<Bound> bounds = new ArrayList<>();
    List<SymTypeVariable> infVars = getInferenceVariables();
    List<SymTypeExpression> typeArgs = getTypeArguments();
    for (int i = 0; i < infVars.size(); i++) {
      if (!typeArgs.get(i).isWildcard()) {
        bounds.add(new TypeEqualityBound(infVars.get(i), typeArgs.get(i)));
      }
    }
    return bounds;
  }

  // Helper

  protected SymTypeExpression calculatePlaceHolder(SymTypeExpression type) {
    SymTypeExpression result;
    if (type.isGenericType()) {
      SymTypeOfGenerics typeGen = type.asGenericType();
      SymTypeOfGenerics declType = typeGen.getDeclaredType();
      List<SymTypeVariable> infVars = new ArrayList<>(typeGen.sizeArguments());
      for (int i = 0; i < typeGen.sizeArguments(); i++) {
        infVars.add(SymTypeExpressionFactory.createTypeVariable(
            SymTypeExpressionFactory.createBottomType(),
            declType.getArgument(i).asTypeVariable().getUpperBound()
        ));
      }
      result = SymTypeExpressionFactory
          .createGenerics(typeGen.getTypeInfo(), new ArrayList<>(infVars));
    }
    else {
      SymTypeOfFunction typeFunc = type.asFunctionType();
      SymTypeOfFunction declType = typeFunc.getDeclaredType();
      List<SymTypeVariable> typeParams = declType.getTypeArguments()
          .stream().map(SymTypeExpression::asTypeVariable)
          .collect(Collectors.toList());
      Map<SymTypeVariable, SymTypeVariable> infVarReplaceMap =
          new TreeMap<>(new SymTypeExpressionComparator());
      for (SymTypeVariable typeParam : typeParams) {
        infVarReplaceMap.put(typeParam,
            SymTypeExpressionFactory.createTypeVariable(
                SymTypeExpressionFactory.createBottomType(),
                typeParam.getUpperBound()
            )
        );
      }
      result = TypeParameterRelations
          .replaceTypeVariables(declType, infVarReplaceMap);
    }
    return result;
  }

  protected List<SymTypeExpression> getTypeArguments(SymTypeExpression type) {
    List<SymTypeExpression> arguments;
    if (type.isGenericType()) {
      arguments = type.asGenericType().getArgumentList();
    }
    else {
      arguments = type.asFunctionType().getTypeArguments();
    }
    return arguments;
  }

  protected SymTypeExpression getDeclaredType() {
    return toBeCaptured.isGenericType() ?
        toBeCaptured.asGenericType().getDeclaredType() :
        toBeCaptured.asFunctionType().getDeclaredType();
  }

}
