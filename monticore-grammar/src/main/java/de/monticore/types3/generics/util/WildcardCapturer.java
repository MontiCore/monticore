package de.monticore.types3.generics.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Capture Conversion (s. Java Spec 20 5.1.10)
 */
public class WildcardCapturer {

  /**
   * Capture conversion, replaces wildcards with fresh type variables.
   * This is not applied recursively.
   * If no type arguments exist, this is the identity relation.
   */
  public <T extends SymTypeExpression> T getCaptureConverted(T type) {
    if (TypeParameterRelations.hasInferenceVariables(type)) {
      Log.error("0xFD201 internal error: Capture conversion is "
          + "only allowed on types without inference variables, but got "
          + type.printFullName()
      );
      return type;
    }
    return internal_getCaptureConverted(type);
  }

  /**
   * {@link #getCaptureConverted(SymTypeExpression)},
   * but ignore inference variables
   */
  protected <T extends SymTypeExpression> T internal_getCaptureConverted(T type) {
    T result;
    if (type.isFunctionType()) {
      result = (T) calculateGetCaptureConverted(type.asFunctionType()).asFunctionType();
    }
    else if (type.isGenericType()) {
      result = (T) calculateGetCaptureConverted(type.asGenericType()).asGenericType();
    }
    else {
      result = (T) type.deepClone();
    }
    return result;
  }

  /**
   * @param func has fixed arity and a symbol with type variables
   */
  protected SymTypeOfFunction calculateGetCaptureConverted(SymTypeOfFunction func) {
    if (func.isElliptic()) {
      Log.error("0xFD303 internal error: "
          + "expected function with fixed arity for capture conversion"
          + ", but got" + func.printFullName()
      );
      return func;
    }
    List<SymTypeVariable> typeParameters = getTypeParameters(func);
    return getCaptureConverted(func, typeParameters);
  }

  /**
   * Capture conversion with an explicit list of type parameters.
   * This required for, e.g., Lambda functions,
   * as they have no symbols to get type parameters from.
   * E.g.,
   * {@code List<? extends Person> -> Person f = (xs) -> xs.get(0);}
   * the lambda function "is declared with the type" T -> R,
   * there T and R are type parameters,
   * and, according to Java Spec 20 15.27.3 the ground target type of
   * {@code List<Person> -> Person}
   */
  public SymTypeOfFunction getCaptureConverted(
      SymTypeOfFunction func,
      List<SymTypeVariable> typeParameters
  ) {
    SymTypeOfFunction result;
    List<SymTypeExpression> includedTypes = new ArrayList<>();
    includedTypes.add(func.getType());
    includedTypes.addAll(func.getArgumentTypeList());
    List<SymTypeExpression> capturedTypes =
        captureWildcards(typeParameters, includedTypes);
    if (func.hasSymbol()) {
      result = SymTypeExpressionFactory.createFunction(
          func.getSymbol(),
          capturedTypes.get(0),
          capturedTypes.subList(1, capturedTypes.size()),
          false
      );
    }
    else {
      result = SymTypeExpressionFactory.createFunction(
          capturedTypes.get(0),
          capturedTypes.subList(1, capturedTypes.size())
      );
    }
    return result;
  }

  protected SymTypeOfGenerics calculateGetCaptureConverted(SymTypeOfGenerics gen) {
    List<SymTypeVariable> typeParameters = getTypeParameters(gen);
    List<SymTypeExpression> capturedTypes =
        captureWildcards(typeParameters, gen.getArgumentList());
    SymTypeOfGenerics result = SymTypeExpressionFactory.createGenerics(
        gen.getTypeInfo(),
        capturedTypes
    );
    return result;
  }

  /**
   * s. Java Spec 20 5.1.10
   */
  protected List<SymTypeExpression> captureWildcards(
      List<SymTypeVariable> typeParameters,
      List<SymTypeExpression> typeArguments
  ) {
    // in the description of Java Spec 20 5.1.10:
    // typeParameters are T1,...,Tn
    // typeArguments are A1,...,An
    // typeArgNoWCs are S1,...,Sn
    // typeArgNoParamRef(i) has the bounds replaced: Ui[A1:=S1,...,An:=Sn]

    if (typeArguments.size() != typeParameters.size()) {
      Log.error("0xFD306 internal error: "
          + "unexpected amount of type arguments");
    }
    // parameters currently cannot have lower bounds
    // e.g., in Java, only upper bounds can be specified, e.g.,
    // <T extends UpperBound> // valid Java
    // <T super LowerBound> // not valid Java
    for (SymTypeVariable param : typeParameters) {
      if (!SymTypeRelations.isBottom(param.getLowerBound())) {
        Log.error("0xFD305 internal error: "
            + "parameter " + param.printFullName()
            + " has unexpected lower bound: "
            + param.getLowerBound().printFullName()
        );
      }
    }

    // type variables to replace parameters in the set of type arguments.
    // WildCards get fresh variables
    List<SymTypeExpression> typeArgNoWCs = new ArrayList<>(typeParameters.size());
    for (int i = 0; i < typeArguments.size(); i++) {
      SymTypeExpression typeArgNoWC;
      if (typeArguments.get(i).isWildcard()) {
        SymTypeOfWildcard wildcard = typeArguments.get(i).asWildcard();
        SymTypeVariable param = typeParameters.get(i);
        if (!wildcard.hasBound()) {
          typeArgNoWC = createCaptureVar(
              SymTypeExpressionFactory.createBottomType(),
              param.getUpperBound().deepClone()
          );
        }
        else if (wildcard.isUpper()) {
          typeArgNoWC = createCaptureVar(
              SymTypeExpressionFactory.createBottomType(),
              // Note: this upper bound may not "make sense", e.g., given:
              // class Car {...} class Person {...}
              // class CarList<T extends Car> {...}
              // CarList<? extends Person> myList;
              // The upper bound is (Car & Person) which is empty.
              // These incidents are to be prevented using CoCos
              SymTypeExpressionFactory.createIntersection(
                  wildcard.getBound().deepClone(),
                  param.getUpperBound().deepClone()
              )
          );
        }
        else {
          typeArgNoWC = createCaptureVar(
              wildcard.getBound().deepClone(),
              param.getUpperBound().deepClone()
          );
        }
      }
      else {
        typeArgNoWC = typeArguments.get(i).deepClone();
      }
      typeArgNoWCs.add(typeArgNoWC);
    }

    // replaces parameter references
    // in the (upper) bounds of the type variables
    Map<SymTypeVariable, SymTypeExpression> replaceMap = Streams.zip(
            typeParameters.stream(),
            typeArgNoWCs.stream(),
            Maps::immutableEntry
        )
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    List<SymTypeExpression> typeArgNoParamRef = typeArgNoWCs.stream()
        .map(t -> TypeParameterRelations.replaceTypeVariables(t, replaceMap))
        .collect(Collectors.toList());

    return typeArgNoParamRef;
  }

  protected List<SymTypeVariable> getTypeParameters(SymTypeOfGenerics gen) {
    SymTypeOfGenerics declaredType =
        SymTypeExpressionFactory.createGenerics(gen.getTypeInfo());
    List<SymTypeVariable> typeParameters = declaredType.getArgumentList()
        .stream().map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    return typeParameters;
  }

  protected List<SymTypeVariable> getTypeParameters(SymTypeOfFunction func) {
    List<SymTypeVariable> typeParameters;
    // this is not possible given a custom (generic) function,
    // that has been added while resolving
    // and that has no symbol / cannot have a symbol (s. OCL flatten).
    // Extension: SymTypeOfFunction with type parameters?
    if (!func.hasSymbol() || func.getSymbol().getTypeVariableList().isEmpty()) {
      Log.error("0xFD304 internal error: "
          + "expected a SymTypeOfFunction with a symbol and type parameters"
          + ", but got " + func.printFullName()
          + (func.hasSymbol() ? " : " + func.getSymbol().getFullName() : "")
      );
      typeParameters = Collections.emptyList();
    }
    else {
      typeParameters =
          func.getSymbol().getTypeVariableList().stream()
              .map(SymTypeExpressionFactory::createTypeVariable)
              .collect(Collectors.toList());
    }
    return typeParameters;
  }

  // Helper

  //should be unique, NO further guarantees
  static int captureVarNextID = 0;

  /**
   * creates a "free" type variable,
   * however, the name is different from others,
   * such that recognizing variables from capture conversion
   * are easier identifiable in Log messages.
   */
  protected SymTypeVariable createCaptureVar(
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    return SymTypeExpressionFactory.createTypeVariable(
        "CAP#" + captureVarNextID++, lowerBound, upperBound
    );
  }

}