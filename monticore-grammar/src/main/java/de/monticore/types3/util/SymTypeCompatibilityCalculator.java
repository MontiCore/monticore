// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.SubTypingBound;
import de.monticore.types3.generics.bounds.TypeEqualityBound;
import de.monticore.types3.generics.bounds.UnsatisfiableBound;
import de.monticore.types3.generics.constraints.TypeEqualityConstraint;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTopType;

/**
 * checks for compatibility between SymTypes
 * delegate of SymTypeRelations
 */
public class SymTypeCompatibilityCalculator {

  protected static final String LOG_NAME = "SymTypeCompatibilityCalculator";

  // Standard interface returning booleans

  public boolean isCompatible(
      SymTypeExpression assignee,
      SymTypeExpression assigner) {
    return constrainCompatible(assignee, assigner).isEmpty();
  }

  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return constrainSubTypeOf(subType, superType).isEmpty();
  }

  /**
   * isSubTypeOf and canBeSubTypeOf, as they are very similar
   * subTypeIsSoft if it is only a possibility, that it is a subtype
   *
   * @deprecated use {@link #constrainSubTypeOf(SymTypeExpression, SymTypeExpression)}
   */
  @Deprecated
  public boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return constrainSubTypeOf(subType, superType).isEmpty();
  }

  @Deprecated
  public boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    return internal_constrainSubTypeOfPreNormalized(subType, superType).isEmpty();
  }

  // Extended interface providing bounds

  public List<Bound> constrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source) {
    List<Bound> result;
    // null is compatible to any non-primitive type
    if (!target.isPrimitive()
        && !target.isNumericWithSIUnitType()
        && !target.isSIUnitType()
        && source.isNullType()) {
      result = Collections.emptyList();
    }
    // subtypes are assignable to their supertypes
    // in addition, we allow boxing
    else {
      SymTypeExpression boxedTarget = de.monticore.types3.SymTypeRelations.box(target);
      SymTypeExpression boxedSource = de.monticore.types3.SymTypeRelations.box(source);
      // important: this does fully work in all cases;
      // as isCompatible (in combination with constrainSubTypeOf)
      // can lead to multiple instantiations for the same type,
      // some boxed, some unboxed.
      // Recommendation is to not mix boxed and unboxed types,
      // however, in combination with Class2MC, this may not be possible.
      result = constrainSubTypeOf(source, target);
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(boxedSource, target);
      }
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(source, boxedTarget);
      }
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(boxedSource, boxedTarget);
      }
    }
    // additionally check regular expressions
    if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
      if (target.isRegExType() || source.isRegExType()) {
        result = regExConstrainCompatible(target, source);
      }
    }
    return result;
  }

  /**
   * isCompatible if one of the arguments is a SymTypeOfRegex
   *
   * @return if false, it can still be compatible due to other typing rules
   */
  protected List<Bound> regExConstrainCompatible(
      SymTypeExpression target,
      SymTypeExpression source) {
    List<Bound> result;
    if (target.isRegExType() && de.monticore.types3.SymTypeRelations.isString(source)) {
      result = Collections.emptyList();
    }
    else if (target.isRegExType() && source.isRegExType()) {
      // note: this is a heuristic, we would need a solution
      // to the inclusion problem for regular expressions.
      // Otherwise, a constrainSubTypeOf-check can be used
      result = Collections.emptyList();
    }
    else {
      // (String, RegEx) is handled in constrainSubTypeOf
      result = Collections.singletonList(getUnsatisfiableBoundForSubTyping(target, source));
    }
    return result;
  }

  public List<Bound> constrainSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    SymTypeExpression normalizedSubType = de.monticore.types3.SymTypeRelations.normalize(subType);
    SymTypeExpression normalizedSuperType = de.monticore.types3.SymTypeRelations.normalize(superType);
    return internal_constrainSubTypeOfPreNormalized(normalizedSubType, normalizedSuperType);
  }

  public List<Bound> internal_constrainSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    // subType union -> all unionized types must be a subtype
    if (subType.isUnionType()) {
      result = constrainSubTypeOfSubUnion(subType.asUnionType(), superType);
    }
    // supertype union -> must be a subtype of any unionized type
    else if (superType.isUnionType()) {
      result = constrainSubTypeOfSuperUnion(subType, superType.asUnionType());
    }
    // supertype intersection -> must be a subtype of all intersected types
    else if (superType.isIntersectionType()) {
      result = constrainSubTypeOfSuperIntersection(subType, superType.asIntersectionType());
    }
    // subType intersection -> any intersected type must be a subtype
    else if (subType.isIntersectionType()) {
      result = constrainSubTypeOfSubIntersection(subType.asIntersectionType(), superType);
    }
    else {
      result = singleConstrainSubTypeOf(subType, superType);
    }
    return result;
  }

  protected List<Bound> constrainSubTypeOfSubUnion(
      SymTypeOfUnion subType,
      SymTypeExpression superType
  ) {
    List<Bound> result = new ArrayList<>();
    for (SymTypeExpression subUType : subType.getUnionizedTypeSet()) {
      result.addAll(internal_constrainSubTypeOfPreNormalized(subUType, superType));
    }
    return result;
  }

  /**
   * @param subType is not a union
   */
  protected List<Bound> constrainSubTypeOfSuperUnion(
      SymTypeExpression subType,
      SymTypeOfUnion superType
  ) {
    List<Bound> result;
    // the sub-lists are connected by 'or's
    List<List<Bound>> results = new ArrayList<>();
    for (SymTypeExpression superUType : superType.getUnionizedTypeSet()) {
      results.add(internal_constrainSubTypeOfPreNormalized(subType, superUType));
    }
    result = handleMultipleListOfBounds(results, subType, superType);
    return result;
  }

  /**
   * @param subType is not a union
   */
  protected List<Bound> constrainSubTypeOfSuperIntersection(
      SymTypeExpression subType,
      SymTypeOfIntersection superType
  ) {
    List<Bound> result = new ArrayList<>();
    for (SymTypeExpression superIType : superType.getIntersectedTypeSet()) {
      result.addAll(internal_constrainSubTypeOfPreNormalized(subType, superIType));
    }
    return result;
  }

  /**
   * @param superType is neither union nor intersection
   */
  protected List<Bound> constrainSubTypeOfSubIntersection(
      SymTypeOfIntersection subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    // special case for inference vars with top type
    if (SymTypeRelations.isTop(subType) &&
        TypeParameterRelations.isInferenceVariable(superType)
    ) {
      result = typeVarConstrainSubTypeOf(subType, superType);
    }
    else {
      // the sub-lists are connected by 'or's
      List<List<Bound>> results = new ArrayList<>();
      for (SymTypeExpression subIType : subType.getIntersectedTypeSet()) {
        results.add(internal_constrainSubTypeOfPreNormalized(subIType, superType));
      }
      result = handleMultipleListOfBounds(results, subType, superType);
    }
    return result;
  }

  /**
   * Helper for
   * {@link #constrainSubTypeOfSuperUnion(SymTypeExpression, SymTypeOfUnion)},
   * {@link #constrainSubTypeOfSubIntersection(SymTypeOfIntersection, SymTypeExpression)}.
   * In these cases, one does not calculate bounds that are connected by 'and',
   * instead, List of 'and'-ed bounds are calculated,
   * which in turn are connected by 'or'.
   * Solving these sets of bounds may be heavily exponential, as such,
   * we use a heuristic to reduce them to a single list of 'and'-ed bounds.
   *
   * @param subType   used for logging
   * @param superType used for logging
   * @return the result may not contain the most optimal set of bounds,
   *     but it will never(!) omit required bounds.
   */
  protected List<Bound> handleMultipleListOfBounds(
      List<List<Bound>> bounds,
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    List<List<Bound>> satisfiableResults = bounds.stream()
        .filter(r -> r.stream().noneMatch(Bound::isUnsatisfiableBound))
        .collect(Collectors.toList());
    if (satisfiableResults.stream().anyMatch(List::isEmpty)) {
      result = Collections.emptyList();
    }
    else if (satisfiableResults.isEmpty()) {
      result = Collections.singletonList(
          getUnsatisfiableBoundForSubTyping(subType, superType)
      );
    }
    else if (satisfiableResults.size() == 1) {
      result = satisfiableResults.get(0);
    }
    else {
      // Warning: Heuristic! (potential false negatives)
      result = Collections
          .min(satisfiableResults, Comparator.comparingInt(List::size));
      Log.debug("Encountered hard to solve subtyping relation:"
              + subType.printFullName() + " <: " + superType.printFullName()
              + ", which may lead to false negatives!"
              + " Arbitrarily choosing bounds:" + System.lineSeparator()
              + printBounds(result),
          LOG_NAME
      );
    }
    return result;
  }

  public List<Bound> constrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    SymTypeExpression normalizedTypeA = de.monticore.types3.SymTypeRelations.normalize(typeA);
    SymTypeExpression normalizedTypeB = de.monticore.types3.SymTypeRelations.normalize(typeB);
    return internal_constrainSameTypePreNormalized(normalizedTypeA, normalizedTypeB);
  }

  protected List<Bound> internal_constrainSameTypePreNormalized(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  ) {
    if (typeA.deepEquals(typeB)) {
      return Collections.emptyList();
    }
    // This CANNOT be (completely) substituted with 2 subtyping constrainments,
    // e.g., given the constraint <G<a> --> G<b>> with a,b are inference vars,
    // the resulting bound has to be a = b.
    // a <: b, b <: a is not considered the same (s. JLS Spec 21 18.3).
    // Additionally, a custom implementation is better wrt. unions/intersections,
    // as those cannot be calculated this well using subtyping constraints
    // (loss of information).
    List<Bound> result;
    if (TypeParameterRelations.isInferenceVariable(typeA)
        || TypeParameterRelations.isInferenceVariable(typeB)) {
      result = typeVarConstrainSameType(typeA, typeB);
    }
    else if (typeA.isIntersectionType() || typeB.isIntersectionType()) {
      result = intersectionConstrainSameType(typeA, typeB);
    }
    else if (typeA.isUnionType() || typeB.isUnionType()) {
      result = unionConstrainSameType(typeA, typeB);
    }
    else if (typeA.isArrayType() && typeB.isArrayType()) {
      result = arrayConstrainSameType(
          (SymTypeArray) typeA,
          (SymTypeArray) typeB
      );
    }
    // unboxed primitives
    // boxed primitives
    // handled by deepEquals-check

    // tuples
    else if (typeA.isTupleType() && typeB.isTupleType()) {
      result = tupleConstrainSameType(
          typeB.asTupleType(),
          typeA.asTupleType()
      );
    }
    // functions
    else if (typeA.isFunctionType() && typeB.isFunctionType()) {
      result = functionConstrainSameType(
          (SymTypeOfFunction) typeA,
          (SymTypeOfFunction) typeB

      );
    }
    // numerics with SIUnit
    else if (
        typeA.isNumericWithSIUnitType() &&
            typeB.isNumericWithSIUnitType()
    ) {
      result = numericWithSIUnitConstrainSameType(
          typeA.asNumericWithSIUnitType(),
          typeB.asNumericWithSIUnitType()
      );
    }
    // siUnits
    else if (typeA.isSIUnitType() && typeB.isSIUnitType()) {
      result = siUnitConstrainSameType(
          typeA.asSIUnitType(),
          typeB.asSIUnitType()
      );
    }
    // regEx
    else if (typeA.isRegExType() || typeB.isRegExType()) {
      result = regExConstrainSameType(typeA, typeB);
    }
    // objects
    else if (
        (typeA.isObjectType() || typeA.isGenericType()) &&
            (typeB.isObjectType() || typeB.isGenericType())) {
      result = objectConstrainSameType(typeA, typeB);
    }
    // wildcards
    else if (typeA.isWildcard() && typeB.isWildcard()) {
      result = wildcardConstrainSameType(typeA.asWildcard(), typeB.asWildcard());
    }
    else {
      result = Collections.singletonList(getUnsatisfiableBoundForSameType(typeA, typeB));
    }
    return result;
  }

  // extension points
  // all extension points expect normalized types as input

  /**
   * this tests all symtypes that are not union or intersection types
   * (and don't contain them)
   */
  protected List<Bound> singleConstrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    // variable
    if (superType.isTypeVariable() || subType.isTypeVariable()) {
      result = typeVarConstrainSubTypeOf(subType, superType);
    }
    // arrays
    else if (superType.isArrayType() && subType.isArrayType()) {
      result = arrayConstrainSubTypeOf(
          (SymTypeArray) subType,
          (SymTypeArray) superType
      );
    }
    // unboxed primitives
    else if (superType.isPrimitive() && subType.isPrimitive()) {
      result = unboxedPrimitiveConstrainSubTypeOf(
          (SymTypePrimitive) subType,
          (SymTypePrimitive) superType
      );
    }
    // boxed primitives
    else if (
        !superType.isPrimitive() && !subType.isPrimitive() &&
            (de.monticore.types3.SymTypeRelations.isNumericType(superType) ||
                de.monticore.types3.SymTypeRelations.isBoolean(superType)) &&
            (de.monticore.types3.SymTypeRelations.isNumericType(subType) ||
                de.monticore.types3.SymTypeRelations.isBoolean(subType))
    ) {
      result = boxedPrimitiveConstrainSubTypeOf(
          (SymTypeOfObject) subType,
          (SymTypeOfObject) superType
      );
    }
    // tuples
    else if (superType.isTupleType() && subType.isTupleType()) {
      result = tupleConstrainSubTypeOf(
          subType.asTupleType(),
          superType.asTupleType()
      );
    }
    // functions
    else if (superType.isFunctionType() && subType.isFunctionType()) {
      result = functionConstrainSubTypeOf(
          (SymTypeOfFunction) subType,
          (SymTypeOfFunction) superType

      );
    }
    // numerics with SIUnit
    else if (
        superType.isNumericWithSIUnitType() &&
            subType.isNumericWithSIUnitType()
    ) {
      result = numericWithSIUnitConstrainSubTypeOf(
          subType.asNumericWithSIUnitType(),
          superType.asNumericWithSIUnitType()
      );
    }
    // siUnits
    else if (superType.isSIUnitType() && subType.isSIUnitType()) {
      result = siUnitConstrainSubTypeOf(
          subType.asSIUnitType(),
          superType.asSIUnitType()
      );
    }
    // regEx
    else if (superType.isRegExType() || subType.isRegExType()) {
      result = regExConstrainSubTypeOf(subType, superType);
    }
    // objects
    else if (
        (superType.isObjectType() || superType.isGenericType()) &&
            (subType.isObjectType() || subType.isGenericType())) {
      result = objectConstrainSubTypeOf(subType, superType);
    }
    else {
      result = Collections.singletonList(getUnsatisfiableBoundForSubTyping(subType, superType));
    }
    return result;
  }

  /**
   * whether one expression is the subType of another,
   * with at least one of the types being a variable.
   */
  protected List<Bound> typeVarConstrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    // T is compatible to T
    // this has to be checked specifically,
    // as two unbounded type variable are not subTypes of each other otherwise
    if (subType.isTypeVariable() &&
        superType.isTypeVariable() &&
        subType.asTypeVariable().denotesSameVar(superType)
    ) {
      result = Collections.emptyList();
    }
    // check upper and lower bound
    else if (subType.isTypeVariable() || superType.isTypeVariable()) {
      SymTypeExpression subUpperBound;
      boolean subTypeIsInfVar;
      if (subType.isTypeVariable()) {
        SymTypeVariable tv = subType.asTypeVariable();
        subUpperBound = tv.getUpperBound();
        subTypeIsInfVar = TypeParameterRelations.isInferenceVariable(tv);
      }
      else {
        subUpperBound = subType;
        subTypeIsInfVar = false;
      }
      SymTypeExpression superLowerBound;
      boolean superTypeIsInfVar;
      if (superType.isTypeVariable()) {
        SymTypeVariable tv = superType.asTypeVariable();
        superLowerBound = tv.getLowerBound();
        superTypeIsInfVar = TypeParameterRelations.isInferenceVariable(tv);
      }
      else {
        superLowerBound = superType;
        superTypeIsInfVar = false;
      }
      if (isSubTypeOf(subUpperBound, superLowerBound)) {
        result = Collections.emptyList();
      }
      else if (!subTypeIsInfVar && !superTypeIsInfVar) {
        result = Collections.singletonList(
            getUnsatisfiableBoundForSubTyping(subType, superType)
        );
      }
      else {
        result = Collections.singletonList(
            new SubTypingBound(subType, superType)
        );
      }
    }
    else {
      Log.error("0xFDB32 internal error, expected an type variable");
      result = Collections.singletonList(
          getUnsatisfiableBoundForSubTyping(subType, superType)
      );
    }
    return result;
  }

  /**
   * whether one array is the subType of another
   */
  protected List<Bound> arrayConstrainSubTypeOf(
      SymTypeArray subArray,
      SymTypeArray superArray
  ) {
    List<Bound> result;
    if (superArray.getDim() != subArray.getDim()) {
      result = Collections.singletonList(new UnsatisfiableBound(
          subArray.printFullName() + " cannot be a subtype of "
              + superArray.printFullName() + " as they have different dimensions"
      ));
    }
    else {
      result = internal_constrainSubTypeOfPreNormalized(
          subArray.getArgument(),
          superArray.getArgument()
      );
    }
    return result;
  }

  /**
   * whether unboxed primitives are in a subType relation
   * s. Java spec 20 4.10.1
   */
  protected List<Bound> unboxedPrimitiveConstrainSubTypeOf(
      SymTypePrimitive subType,
      SymTypePrimitive superType
  ) {
    return primitiveConstrainSubTypeOf(subType, superType);
  }

  /**
   * whether a boxed primitive is the subType of another
   * This version is NOT conform to the java spec (20);
   * Java does not allow, e.g., Integer i = 2; Float f = i;
   * Note, that this is (nearly) never an issue about losing object identity:
   * E.g., Integer x = 2; Integer y = 2; // x == y holds
   * but, Integer x = 222; Integer y = 222; // x != y holds
   * (tested in JDK 17)
   * s.a. Java spec 20 5.1.7
   * Thus, unlike Java we allow Integer i = 2; Float f = i;,
   * is a subTypeRelation analoge to unboxed primitives
   * This method needs to be overridden
   * if a more Java-conform check is required
   */
  protected List<Bound> boxedPrimitiveConstrainSubTypeOf(
      SymTypeOfObject subType,
      SymTypeOfObject superType
  ) {
    return primitiveConstrainSubTypeOf(subType, superType);
  }

  protected List<Bound> primitiveConstrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    if (de.monticore.types3.SymTypeRelations.isBoolean(superType) &&
        de.monticore.types3.SymTypeRelations.isBoolean(subType)) {
      result = Collections.emptyList();
    }
    else if (de.monticore.types3.SymTypeRelations.isNumericType(superType) &&
        de.monticore.types3.SymTypeRelations.isNumericType(subType)) {
      if (de.monticore.types3.SymTypeRelations.isDouble(superType)) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isFloat(superType) &&
          (de.monticore.types3.SymTypeRelations.isFloat(subType) ||
              de.monticore.types3.SymTypeRelations.isIntegralType(subType)
          )) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isLong(superType) &&
          de.monticore.types3.SymTypeRelations.isIntegralType(
              subType)) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isInt(superType) &&
          de.monticore.types3.SymTypeRelations.isIntegralType(subType) &&
          !de.monticore.types3.SymTypeRelations.isLong(subType)) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isChar(superType) &&
          de.monticore.types3.SymTypeRelations.isChar(subType)) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isShort(superType) &&
          (de.monticore.types3.SymTypeRelations.isShort(subType)
              || de.monticore.types3.SymTypeRelations.isByte(subType))) {
        result = Collections.emptyList();
      }
      else if (de.monticore.types3.SymTypeRelations.isByte(superType) &&
          de.monticore.types3.SymTypeRelations.isByte(subType)) {
        result = Collections.emptyList();
      }
      else {
        result = Collections.singletonList(
            getUnsatisfiableBoundForSubTyping(subType, superType));
      }
    }
    else {
      result = Collections.singletonList(
          getUnsatisfiableBoundForSubTyping(subType, superType));
    }
    return result;
  }

  protected List<Bound> tupleConstrainSubTypeOf(
      SymTypeOfTuple subTuple,
      SymTypeOfTuple superTuple
  ) {
    List<Bound> result = new ArrayList<>();
    if (subTuple.sizeTypes() != superTuple.sizeTypes()) {
      result.add(new UnsatisfiableBound(
          subTuple.printFullName() + " cannot be a subtype of "
              + superTuple.printFullName() + " as they have different lengths"
      ));
    }
    else {
      for (int i = 0; i < subTuple.sizeTypes(); i++) {
        result.addAll(internal_constrainSubTypeOfPreNormalized(
            subTuple.getType(i),
            superTuple.getType(i))
        );
      }
    }
    return result;
  }

  protected List<Bound> functionConstrainSubTypeOf(
      SymTypeOfFunction subFunc,
      SymTypeOfFunction superFunc
  ) {
    List<Bound> result = new ArrayList<>();
    // return type
    if (subFunc.getType().isVoidType() != superFunc.getType().isVoidType()) {
      result.add(new UnsatisfiableBound(subFunc.printFullName()
          + " is not a subtype of " + superFunc.printFullName()
          + " as only one of them has a return type (and the other has void)."
      ));
    }
    else if (!subFunc.getType().isVoidType()
        && !superFunc.getType().isVoidType()
    ) {
      // return types: co-variant
      result.addAll(internal_constrainSubTypeOfPreNormalized(subFunc.getType(), superFunc.getType()));
    }
    // if the super-function is elliptic, the sub one must be as well
    if (!subFunc.isElliptic() && superFunc.isElliptic()) {
      result.add(new UnsatisfiableBound(subFunc.printFullName()
          + " is not a subtype of " + superFunc.printFullName()
          + " as it is not elliptic and the supertype is."
      ));
    }
    // if they are not elliptic, the number of arguments must be the same
    if (!(subFunc.isElliptic() || superFunc.isElliptic() ||
        subFunc.sizeArgumentTypes() == superFunc.sizeArgumentTypes())) {
      result.add(new UnsatisfiableBound(subFunc.printFullName()
          + " is not a subtype of " + superFunc.printFullName()
          + " as they do not have the same amount of parameters."
      ));
    }
    // check if all arguments are compatible
    int argsToCheck = Math.max(
        superFunc.sizeArgumentTypes(),
        subFunc.isElliptic() ?
            subFunc.sizeArgumentTypes() - 1 :
            subFunc.sizeArgumentTypes()
    );
    for (int i = 0; i < argsToCheck; i++) {
      SymTypeExpression subParamType =
          subFunc.isEmptyArgumentTypes() ?
              createObscureType() :
              subFunc.getArgumentType(
                  Math.min(i, subFunc.sizeArgumentTypes() - 1)
              );
      SymTypeExpression superParamType =
          superFunc.isEmptyArgumentTypes() ?
              createObscureType() :
              superFunc.getArgumentType(
                  Math.min(i, superFunc.sizeArgumentTypes() - 1)
              );
      // argument types: contra-variant
      result.addAll(internal_constrainSubTypeOfPreNormalized(
          superParamType, subParamType
      ));
    }
    return result;
  }

  /**
   * whether one object is a subType of another
   * this includes SymTypeOfObject, as well as SymTypeOfGenerics
   */
  protected List<Bound> objectConstrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    String superName;
    List<SymTypeExpression> superArgs;
    if (superType.isGenericType()) {
      superName = ((SymTypeOfGenerics) superType).getTypeConstructorFullName();
      superArgs = ((SymTypeOfGenerics) superType).getArgumentList();
    }
    else {
      superName = superType.printFullName();
      superArgs = new ArrayList<>();
    }
    String subName;
    List<SymTypeExpression> subArgs;
    if (subType.isGenericType()) {
      subName = ((SymTypeOfGenerics) subType).getTypeConstructorFullName();
      subArgs = ((SymTypeOfGenerics) subType).getArgumentList();
    }
    else {
      subName = subType.printFullName();
      subArgs = new ArrayList<>();
    }
    if (subName.equals(superName)) {
      if (subArgs.size() != superArgs.size()) {
        String errorMessage = "0xFD6AD internal error: type \"" + subName + "\" "
            + "used with inconsistent amount of type arguments: "
            + subType.printFullName() + " and "
            + superType.printFullName();
        Log.error(errorMessage);
        return Collections.singletonList(new UnsatisfiableBound(errorMessage));
      }
      // cannot use subTyping-relation for type arguments;
      // List<SuperClass> is not a superType of List<SubClass> or vice versa.
      // However, e.g., List<? super C> is a superType of List<C>
      result = new ArrayList<>();
      for (int i = 0; i < subArgs.size(); i++) {
        result.addAll(constrainContainsPreNormalized(subArgs.get(i), superArgs.get(i)));
      }
    }
    else {
      // check superclasses

      // this can be improved if required:
      // E.g.: S ?<: D
      // SuperTypes(S) = A,B,C
      // A !<: D, B <: D, C <: D
      // -> which bounds do we get?
      // B AND C?

      boolean isSatisfiable = false;
      result = new ArrayList<>();
      List<Bound> unsatisfiableResult = new ArrayList<>();
      List<SymTypeExpression> subSuperTypes = getSuperTypes(subType);
      for (SymTypeExpression subSuperExpr : subSuperTypes) {
        // here we do not box the symTypes(, as we did not before)
        // this would make List<int> be a subtype of java.util.List<Integer>,
        // as lists of primitives are not available in java,
        // they are interpreted as being mostly equivalent,
        // however, we if we were to box them here,
        // our type inference would infer wrong bounds.
        List<Bound> superCheckBounds = internal_constrainSubTypeOfPreNormalized(
            de.monticore.types3.SymTypeRelations.normalize(
                boxGenericButNotArguments(subSuperExpr)
            ),
            boxGenericButNotArguments(superType)
        );
        boolean superIsSatisfiable = superCheckBounds.stream()
            .noneMatch(Bound::isUnsatisfiableBound);
        if (superIsSatisfiable) {
          result.addAll(superCheckBounds);
          isSatisfiable = true;
        }
        else if (!superIsSatisfiable && !isSatisfiable) {
          unsatisfiableResult.addAll(superCheckBounds);
        }
      }
      if (!isSatisfiable) {
        // note: a lot of info:
        result.addAll(unsatisfiableResult);
        result.add(getUnsatisfiableBoundForSubTyping(subType, superType));
      }
    }
    return result;
  }

  /**
   * whether one type is the subType of another is
   * if at least one of them is a SymTypeOfRegEx
   */
  protected List<Bound> regExConstrainSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    List<Bound> result;
    if (de.monticore.types3.SymTypeRelations.isString(superType) && subType.isRegExType()) {
      result = Collections.emptyList();
    }
    else if (superType.isRegExType()) {
      if (subType.isRegExType()) {
        // this is incomplete,
        // R"(a|e)" can be considered a subtype of R"(a|e|o)".
        // R"(a|e)y", R"(ay|ey)" are subtypes of each other.
        // And each regex is a subtype of itself.
        // However, this would require an implementation
        // that solves the inclusion problem for regular expressions...
        // s.a. "The Inclusion Problem for Regular Expressions"
        //   - Dag Hovland (2010)
        if (subType.deepEquals(superType)) {
          result = Collections.emptyList();
        }
        else {
          result = Collections.singletonList(
              getUnsatisfiableBoundForSubTyping(subType, superType)
          );
        }
      }
      else {
        result = Collections.singletonList(getUnsatisfiableBoundForSubTyping(subType, superType));
      }
    }
    else {
      result = Collections.singletonList(getUnsatisfiableBoundForSubTyping(subType, superType));
    }
    return result;
  }

  protected List<Bound> numericWithSIUnitConstrainSubTypeOf(
      SymTypeOfNumericWithSIUnit subType,
      SymTypeOfNumericWithSIUnit superType
  ) {
    // note: here, there is no dimension of one,
    // as they get removed during normalization
    List<Bound> result = new ArrayList<>();
    result.addAll(siUnitConstrainSubTypeOf(subType.getSIUnitType(), superType.getSIUnitType()));
    result.addAll(constrainSubTypeOf(subType.getNumericType(), superType.getNumericType()));
    return result;
  }

  protected List<Bound> siUnitConstrainSubTypeOf(
      SymTypeOfSIUnit subType,
      SymTypeOfSIUnit superType
  ) {
    List<Bound> result;
    // order is not important; Here, the subtyping relation is symmetric
    SymTypeOfSIUnit inverse = SIUnitTypeRelations.invert(subType);
    SymTypeOfSIUnit divided = SIUnitTypeRelations.multiply(superType, inverse);
    if (SIUnitTypeRelations.isOfDimensionOne(divided)) {
      result = Collections.emptyList();
    }
    else {
      result = Collections.singletonList(
          getUnsatisfiableBoundForSubTyping(subType, superType)
      );
    }
    return result;
  }

  protected List<Bound> typeVarConstrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    List<Bound> result;
    if (!typeA.isTypeVariable() && !typeB.isTypeVariable()) {
      Log.error("0xFDC32 internal error, expected an type variable");
      result = Collections.singletonList(
          getUnsatisfiableBoundForSameType(typeA, typeB)
      );
    }
    else if (typeA.isTypeVariable() && typeA.asTypeVariable().denotesSameVar(typeB)) {
      result = Collections.emptyList();
    }
    else if (TypeParameterRelations.isInferenceVariable(typeA)
        || TypeParameterRelations.isInferenceVariable(typeB)
    ) {
      result = Collections.singletonList(
          new TypeEqualityBound(typeA, typeB)
      );
    }
    else {
      result = Collections.singletonList(
          getUnsatisfiableBoundForSameType(typeA, typeB)
      );
    }
    return result;
  }

  protected List<Bound> intersectionConstrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    // special often relevant case:
    if ((SymTypeRelations.isTop(typeA) && !SymTypeRelations.isTop(typeB)) ||
        !SymTypeRelations.isTop(typeA) && SymTypeRelations.isTop(typeB)
    ) {
      return Collections.singletonList(
          getUnsatisfiableBoundForSameType(typeA, typeB)
      );
    }
    return constrainSameTypeUsingSubTypeOf(typeA, typeB);
  }

  protected List<Bound> unionConstrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    // special often relevant case:
    if ((SymTypeRelations.isBottom(typeA) && !SymTypeRelations.isBottom(typeB)) ||
        !SymTypeRelations.isBottom(typeA) && SymTypeRelations.isBottom(typeB)
    ) {
      return Collections.singletonList(
          getUnsatisfiableBoundForSameType(typeA, typeB)
      );
    }
    return constrainSameTypeUsingSubTypeOf(typeA, typeB);
  }

  protected List<Bound> arrayConstrainSameType(SymTypeArray arrayA, SymTypeArray arrayB) {
    List<Bound> result;
    if (arrayA.getDim() != arrayB.getDim()) {
      result = Collections.singletonList(new UnsatisfiableBound(
          arrayA.printFullName() + " cannot be the same type as "
              + arrayB.printFullName() + " as they have different dimensions"
      ));
    }
    else {
      result = internal_constrainSameTypePreNormalized(
          arrayB.getArgument(),
          arrayA.getArgument()
      );
    }
    return result;
  }

  protected List<Bound> tupleConstrainSameType(SymTypeOfTuple tupleA, SymTypeOfTuple tupleB) {
    List<Bound> result = new ArrayList<>();
    if (tupleA.sizeTypes() != tupleB.sizeTypes()) {
      result.add(new UnsatisfiableBound(
          tupleA.printFullName() + " cannot be the same type as "
              + tupleB.printFullName() + " as they have different lengths"
      ));
    }
    else {
      for (int i = 0; i < tupleA.sizeTypes(); i++) {
        result.addAll(internal_constrainSameTypePreNormalized(
            tupleA.getType(i),
            tupleB.getType(i))
        );
      }
    }
    return result;
  }

  protected List<Bound> functionConstrainSameType(SymTypeOfFunction funcA, SymTypeOfFunction funcB) {
    List<Bound> result = new ArrayList<>();
    // return type
    result.addAll(internal_constrainSameTypePreNormalized(funcA.getType(), funcB.getType()));
    // either both are elliptic or none are
    if (funcA.isElliptic() != funcB.isElliptic()) {
      result.add(new UnsatisfiableBound(funcA.printFullName()
          + " is not the same type as " + funcB.printFullName()
          + " as exactly one is elliptic."
      ));
    }
    // the number of arguments must be the same
    else if (funcA.sizeArgumentTypes() != funcB.sizeArgumentTypes()) {
      result.add(new UnsatisfiableBound(funcA.printFullName()
          + " is not the same type as " + funcB.printFullName()
          + " as they do not have the same amount of parameters."
      ));
    }
    else {
      for (int i = 0; i < funcA.sizeArgumentTypes(); i++) {
        result.addAll(internal_constrainSameTypePreNormalized(
            funcA.getArgumentType(i),
            funcB.getArgumentType(i)
        ));
      }
    }
    return result;
  }

  protected List<Bound> numericWithSIUnitConstrainSameType(SymTypeOfNumericWithSIUnit numericWithSIUnitA, SymTypeOfNumericWithSIUnit numericWithSIUnitB) {
    return constrainSameTypeUsingSubTypeOf(numericWithSIUnitA, numericWithSIUnitB);
  }

  protected List<Bound> siUnitConstrainSameType(SymTypeOfSIUnit siUnitA, SymTypeOfSIUnit siUnitB) {
    return constrainSameTypeUsingSubTypeOf(siUnitA, siUnitB);
  }

  protected List<Bound> regExConstrainSameType(SymTypeExpression regexA, SymTypeExpression regexB) {
    return constrainContainsPreNormalized(regexA, regexB);
  }

  protected List<Bound> objectConstrainSameType(SymTypeExpression typeA, SymTypeExpression typeB) {
    List<Bound> result;
    String nameA;
    List<SymTypeExpression> argsA;
    if (typeA.isGenericType()) {
      nameA = ((SymTypeOfGenerics) typeA).getTypeConstructorFullName();
      argsA = ((SymTypeOfGenerics) typeA).getArgumentList();
    }
    else {
      nameA = typeA.printFullName();
      argsA = new ArrayList<>();
    }
    String nameB;
    List<SymTypeExpression> argsB;
    if (typeB.isGenericType()) {
      nameB = ((SymTypeOfGenerics) typeB).getTypeConstructorFullName();
      argsB = ((SymTypeOfGenerics) typeB).getArgumentList();
    }
    else {
      nameB = typeB.printFullName();
      argsB = new ArrayList<>();
    }
    if (!nameB.equals(nameA)) {
      result = Collections.singletonList(new UnsatisfiableBound(
          typeA.printFullName() + " is not the same type as "
              + typeB.printFullName() + " as they have different names"
      ));
    }
    else if (argsB.size() != argsA.size()) {
      String errorMessage = "0xFD6AE internal error: type \"" + nameA + "\" "
          + "used with inconsistent amount of type arguments: "
          + typeB.printFullName() + " and "
          + typeA.printFullName();
      Log.error(errorMessage);
      return Collections.singletonList(new UnsatisfiableBound(errorMessage));
    }
    else {
      result = new ArrayList<>();
      for (int i = 0; i < argsB.size(); i++) {
        result.addAll(internal_constrainSameTypePreNormalized(argsA.get(i), argsB.get(i)));
      }
    }
    return result;
  }

  protected List<Bound> wildcardConstrainSameType(SymTypeOfWildcard wcA, SymTypeOfWildcard wcB) {
    List<Bound> result;
    if (wcA.hasBound() != wcB.hasBound() || wcA.isUpper() != wcB.isUpper()) {
      result = Collections.singletonList(new UnsatisfiableBound(
          wcA.printFullName() + " is not the same type as "
              + wcB.printFullName() + " as they have different types of bounds."
      ));
    }
    else {
      // both habe bounds, as otherwise,
      // the deepEquals check would have caught this case
      result = internal_constrainSameTypePreNormalized(wcA.getBound(), wcB.getBound());
    }
    return result;
  }

  // Helper

  /**
   * Is the set of types denoted by subSetType a subSet
   * of the set of types denoted by superSetType?
   * s. Java spec 20 4.5.1, 4.10.2
   * A type variable represents multiple Types,
   * in other cases, this is an identity check.
   * s. a. {@link #typeVarConstrainSubTypeOf}
   * The arguments are expected to be normalized.
   * Additionally, type variables within other symTypes
   * are currently not supported,
   * this helper function (currently) is only to check subtyping of generics.
   * <p>
   * Fundamentally, T1 "contains" T2 ("T2 <= T1")
   * if the set of types denoted by T1 is (provably) a superSet
   * of the types denoted by T2.
   * This translates to the reflexive and transitive closure of (from spec):
   * <ul>
   * <li> ? extends T <= ? extends S if T <: S
   * <li> ? extends T <= ?
   * <li> ? super T <= ? super S if S <: T
   * <li> ? super T <= ?
   * <li> ? super T <= ? extends Object
   * <li> T <= T
   * <li> T <= ? extends T
   * <li> T <= ? super T
   * </ul>
   */
  protected List<Bound> constrainContainsPreNormalized(
      SymTypeExpression subSetType,
      SymTypeExpression superSetType
  ) {
    List<Bound> result;
    // stop recursive checks (e.g., A<T extends A<T>>)
    if (subSetType.deepEquals(superSetType)) {
      result = Collections.emptyList();
    }
    // superSetType is not a wildcard -> create better constraints for this case
    else if (!superSetType.isWildcard()) {
      if (subSetType.isWildcard()) {
        result = Collections.singletonList(
            getUnsatisfiableBoundForContainment(subSetType, superSetType)
        );
      }
      else {
        result = internal_constrainSameTypePreNormalized(subSetType, superSetType);
      }
    }
    // check bounds of wildcard,
    // does not simply go over all applicable combinations of bounds,
    // as to reduce the number of constraints needed to be checked.
    else {
      SymTypeOfWildcard superSetWC = superSetType.asWildcard();
      // <S <= ?>
      if (!superSetWC.hasBound()) {
        result = Collections.emptyList();
      }
      // <S <= ? extends T>
      else if (superSetWC.isUpper()) {
        if (!subSetType.isWildcard()) {
          result = constrainSubTypeOf(subSetType, superSetWC.getBound());
        }
        else {
          SymTypeOfWildcard subSetWC = subSetType.asWildcard();
          // note: we once constrain with #TOP <: a
          // and once with #TOP = a
          // this aligns with Java Spec 21 18.2.3
          // <? <= ? extends T>
          if (!subSetWC.hasBound()) {
            result = constrainSubTypeOf(createTopType(), superSetWC.getBound());
          }
          // <? extends S' <= ? extends T>
          else if (subSetWC.isUpper()) {
            result = constrainSubTypeOf(subSetWC.getBound(), superSetWC.getBound());
          }
          // <? super S' <= ? extends T>
          else {
            result = constrainSameType(createTopType(), superSetWC.getBound());
          }
        }
      }
      // <S <= ? super T>
      else {
        // <S <= ? super T>
        if (!subSetType.isWildcard()) {
          result = constrainSubTypeOf(superSetWC.getBound(), subSetType);
        }
        // <? super S' <= ? super T>
        else if (!subSetType.asWildcard().isUpper()) {
          result = constrainSubTypeOf(
              superSetWC.getBound(),
              subSetType.asWildcard().getBound()
          );
        }
        // <? extends S' <= ? super T>
        else {
          result = Collections.singletonList(
              getUnsatisfiableBoundForContainment(subSetType, superSetType)
          );
        }
      }
    }
    return result;
  }

  /**
   * Reduces a constraint <a = b> to the constraints <a <: b>, <b <: a>.
   * This is not necessarily ideal wrt. resulting messages,
   * and should be replaced in the future if required.
   * It will most likely result in incorrect values,
   * iff any inference variables are present.
   */
  protected List<Bound> constrainSameTypeUsingSubTypeOf(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  ) {
    List<Bound> result = new ArrayList<>();
    result.addAll(internal_constrainSubTypeOfPreNormalized(typeA, typeB));
    result.addAll(internal_constrainSubTypeOfPreNormalized(typeB, typeA));
    // only happens if any type includes inference variables
    if (!result.isEmpty()) {
      Log.error("0xFDCAF (internal) error: Constraint to complex"
          + " to evaluate with the current implementation: "
          + new TypeEqualityConstraint(typeA, typeB).print()
          + ". This part of the implementation can be extended if required."
      );
    }
    return result;
  }

  protected UnsatisfiableBound getUnsatisfiableBoundForSubTyping(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    return new UnsatisfiableBound(
        subType.printFullName() + " is not a subtype of "
            + superType.printFullName() + "."
    );
  }

  protected UnsatisfiableBound getUnsatisfiableBoundForSameType(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  ) {
    return new UnsatisfiableBound(
        typeA.printFullName() + " is not the same type as "
            + typeB.printFullName() + "."
    );
  }

  protected UnsatisfiableBound getUnsatisfiableBoundForContainment(
      SymTypeExpression subSetType,
      SymTypeExpression superSetType
  ) {
    return new UnsatisfiableBound(
        "Type argument " + subSetType.printFullName()
            + " cannot be contained in type argument "
            + superSetType.printFullName() + "."
    );
  }

  protected List<SymTypeExpression> getSuperTypes(SymTypeExpression thisType) {
    return SymTypeRelations.getNominalSuperTypes(thisType);
  }

  /**
   * E.g.: {@code List<List<int>> to java.util.List<List<int>>}
   */
  protected <T extends SymTypeExpression> T boxGenericButNotArguments(T type) {
    if (!type.isGenericType()) {
      return type;
    }
    else {
      SymTypeOfGenerics original = type.asGenericType();
      SymTypeOfGenerics result = SymTypeRelations.box(type).asGenericType();
      for (int i = 0; i < original.sizeArguments(); i++) {
        result.setArgument(i, original.getArgument(i).deepClone());
      }
      return (T) result;
    }
  }

  protected String printBounds(List<Bound> bounds) {
    return bounds.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }

}
