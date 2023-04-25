/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;

public class TypeRelations2 {

  protected SymTypeUnboxingVisitor unboxingVisitor;

  protected SymTypeNormalizeVisitor normalizeVisitor;

  public TypeRelations2() {
    this.unboxingVisitor = new SymTypeUnboxingVisitor();
    this.normalizeVisitor = new SymTypeNormalizeVisitor();
    normalizeVisitor.setTypeRelations(this);
  }

  public boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assignor) {
    boolean result;
    // null is compatible to any non-primitive type
    if (!assignee.isPrimitive() && assignor.isNullType()) {
      result = true;
    }
    // subtypes are assignable to their supertypes
    // additionaly, we allow unboxing
    else {
      SymTypeExpression unboxedAssignee = unbox(assignee);
      SymTypeExpression unboxedAssignor = unbox(assignor);
      result = canBeSubTypeOf(unboxedAssignor, unboxedAssignee);
    }
    return result;
  }

  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return isSubTypeOf(subType, superType, false);
  }

  /**
   * a type can be a subtype of a variable. but it is not required to be
   */
  public boolean canBeSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return isSubTypeOf(subType, superType, true);
  }

  /**
   * isSubTypeOf and canBeSubTypeOf, as they are very similar
   * subTypeIsSoft if it is only a possibility, that it is a subtype
   */
  public boolean isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    SymTypeExpression normalizedSubType = normalize(subType);
    SymTypeExpression normalizedSuperType = normalize(superType);
    return isSubTypeOfPreNormalized(
        normalizedSubType,
        normalizedSuperType,
        subTypeIsSoft
    );
  }

  /**
   * isSubTypeOf, but the types are expected to be normalized
   * necessary to avoid loops
   */
  public boolean isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    boolean result;
    // variable
    if (superType.isTypeVariable() || subType.isTypeVariable()) {
      result = subTypeIsSoft;
    }
    // subType union -> all unionized types must be a subtype
    else if (subType.isUnionType()) {
      result = true;
      for (SymTypeExpression subUType :
          ((SymTypeOfUnion) subType).getUnionizedTypeSet()) {
        result = result
            && isSubTypeOfPreNormalized(subUType, superType, subTypeIsSoft);
      }
    }
    // supertype union -> must be a subtype of any unionized type
    else if (superType.isUnionType()) {
      result = false;
      for (SymTypeExpression superUType :
          ((SymTypeOfUnion) superType).getUnionizedTypeSet()) {
        result = result
            || isSubTypeOfPreNormalized(subType, superUType, subTypeIsSoft);
      }
    }
    // supertype intersection -> must be a subtype of all intersected types
    else if (superType.isIntersectionType()) {
      result = true;
      for (SymTypeExpression superIType :
          ((SymTypeOfIntersection) superType).getIntersectedTypeSet()) {
        result = result
            && isSubTypeOfPreNormalized(subType, superIType, subTypeIsSoft);
      }
    }
    // subType intersection -> any intersected type must be a subtype
    else if (subType.isIntersectionType()) {
      result = false;
      for (SymTypeExpression subIType :
          ((SymTypeOfIntersection) subType).getIntersectedTypeSet()) {
        result = result
            || isSubTypeOfPreNormalized(subIType, superType, subTypeIsSoft);
      }
    }
    // arrays
    else if (superType.isArrayType() && subType.isArrayType()) {
      SymTypeArray superArray = (SymTypeArray) superType;
      SymTypeArray subArray = (SymTypeArray) subType;
      result = superArray.getDim() == subArray.getDim() &&
          isSubTypeOfPreNormalized(
              superArray.getArgument(),
              subArray.getArgument(),
              subTypeIsSoft
          );
    }
    // primitives
    else if (superType.isPrimitive() && subType.isPrimitive()) {
      if (isBoolean(superType) && isBoolean(subType)) {
        result = true;
      }
      else if (isNumericType(superType) && isNumericType(subType)) {
        if (isDouble(superType)) {
          result = true;
        }
        else if (isFloat(superType) &&
            (isFloat(subType) || isIntegralType(subType))) {
          result = true;
        }
        else if (isLong(superType) && isIntegralType(subType)) {
          result = true;
        }
        else if (isInt(superType) &&
            isIntegralType(subType) &&
            !isLong(subType)) {
          result = true;
        }
        else if (isChar(superType) && isChar(subType)) {
          result = true;
        }
        else if (isShort(superType) && (isShort(subType) || isByte(subType))) {
          result = true;
        }
        else if (isByte(superType) && isByte(subType)) {
          result = true;
        }
        else {
          result = false;
        }
      }
      else {
        result = false;
      }
    }
    // functions
    else if (superType.isFunctionType() && subType.isFunctionType()) {
      SymTypeOfFunction superFunc = (SymTypeOfFunction) superType;
      SymTypeOfFunction subFunc = (SymTypeOfFunction) subType;
      result = true;
      // return type
      result = result &&
          isSubTypeOfPreNormalized(superFunc.getType(), subFunc.getType(), subTypeIsSoft);
      // if the super function is elliptic, the sub one must be as well
      result = result && (subFunc.isElliptic() || !superFunc.isElliptic());
      // if they are not elliptic, the number of arguments must be the same
      result = result && (subFunc.isElliptic() ||
          subFunc.sizeArgumentTypes() == superFunc.sizeArgumentTypes());
      // check if all arguments are compatible
      int argsToCheck = Math.max(
          superFunc.sizeArgumentTypes(),
          subFunc.isElliptic() ?
              subFunc.sizeArgumentTypes() - 1 :
              subFunc.sizeArgumentTypes()
      );
      for (int i = 0; result && i < argsToCheck; i++) {
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
        result = result && isSubTypeOfPreNormalized(
            subParamType, superParamType, subTypeIsSoft
        );
      }
    }
    // objects
    else if (
        (superType.isObjectType() || superType.isGenericType()) &&
            (subType.isObjectType() || subType.isGenericType())) {
      TypeSymbol superSym = superType.getTypeInfo();
      TypeSymbol subSym = subType.getTypeInfo();
      List<SymTypeExpression> superArgs;
      if (superType.isGenericType()) {
        superArgs = ((SymTypeOfGenerics) superType).getArgumentList();
      }
      else {
        superArgs = new ArrayList<>();
      }
      List<SymTypeExpression> subArgs;
      if (subType.isGenericType()) {
        subArgs = ((SymTypeOfGenerics) subType).getArgumentList();
      }
      else {
        subArgs = new ArrayList<>();
      }
      //TODO check recursively, this is only a hotfix, see #2977
      if (subSym.getFullName().equals(superSym.getFullName())) {
        result = true;
        for (int i = 0; i < subArgs.size(); i++) {
          if (!subArgs.get(i).isTypeVariable()) {
            if (!(subArgs.get(i).print().equals(superArgs.get(i).print()))) {
              result = false;
            }
          }
        }
      }
      else {
        // check super classes
        result = false;
        for (SymTypeExpression subSuperExpr : subSym.getSuperTypesList()) {
          result = result ||
              isSubTypeOfPreNormalized(
                  normalize(unbox(subSuperExpr)),
                  superType,
                  subTypeIsSoft
              );
        }
      }
    }
    else {
      result = false;
    }
    return result;
  }

  public Optional<SymTypeExpression> leastUpperBound(
      SymTypeExpression... types) {
    return leastUpperBound(List.of(types));
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
    Optional<SymTypeExpression> lub;
    // the function may be extended to calculate further upper bounds
    // e.g. in Java:
    // lub(A, B[]) is lub(A, arraySuperType)

    // normalize the types
    // we rely on the types being structured
    // unions on first level, intersections on second
    // and arrays on the third
    Set<SymTypeExpression> typeSet = types.stream()
        .map(this::normalize)
        .collect(Collectors.toSet());

    // unpack unions
    Set<SymTypeExpression> typesSet_tmp = typeSet;
    typeSet = new HashSet<>();
    for (SymTypeExpression type : typesSet_tmp) {
      if (type.isUnionType()) {
        typeSet.addAll(((SymTypeOfUnion) type).getUnionizedTypeSet());
      }
      else {
        typeSet.add(type);
      }
    }

    // now that we removed the top level unions,
    // remove top level and intersection level variables,
    // without removing the top level intersections yet
    // we can do this as we do not plan to support constraint solving
    typesSet_tmp = typeSet;
    typeSet = new HashSet<>();
    for (SymTypeExpression type : typesSet_tmp) {
      if (type.isIntersectionType()) {
        SymTypeOfIntersection inter = (SymTypeOfIntersection) type;
        inter.getIntersectedTypeSet()
            .removeIf(SymTypeExpression::isTypeVariable);
        typeSet.add(normalize(inter));
      }
      else if (type.isTypeVariable()) {
        // no-op
      }
      else {
        typeSet.add(type);
      }
    }

    // only used once to check if all are equal
    final SymTypeExpression typeNonUnionTmp = typeSet.stream().findFirst().get();
    // lub without any information
    if (typeSet.isEmpty()) {
      // lub of only variables (or nothing) is unbounded
      lub = Optional.empty();
    }
    // lub of all the same type
    else if (typeSet.stream().allMatch(t ->
        typeNonUnionTmp.deepEquals(t))) {
      lub = Optional.of(typeSet.stream().findFirst().get().deepClone());
    }
    // at least two different types, try (boxed) primitives first
    // lub of boolean
    else if (typeSet.stream().allMatch(t -> isBoolean(t))) {
      // note: at least one is not boxed, so unbox all
      lub = Optional.of(createPrimitive(BasicSymbolsMill.BOOLEAN));
    }
    // lub of number
    // based on Java Spec (20): Numeric Conditional Expressions
    else if (typeSet.stream().allMatch(t -> isNumericType(t))) {
      Stream<SymTypeExpression> numbers = typeSet.stream().map(this::unbox);
      // lub of byte
      if (numbers.allMatch(t -> isByte(t))) {
        lub = Optional.of(createPrimitive(BasicSymbolsMill.BYTE));
      }
      // lub of byte|short
      else if (numbers.allMatch(t -> isByte(t)
          || isShort(t))) {
        lub = Optional.of(createPrimitive(BasicSymbolsMill.SHORT));
      }
      // lub using numeric promotion
      else {
        lub = Optional.of(numericPromotion(numbers.collect(Collectors.toList())));
      }
    }
    // lub of incompatible set of primitives
    else if (typeSet.stream().allMatch(t ->
        isNumericType(t) ||
            isBoolean(t))) {
      lub = Optional.of(createObscureType()); // or empty?
    }
    // here the function may be extended to calculate further upper bounds
    // cases involving subtyping
    else {
      // lub may be an intersection, e.g. two interfaces,
      // start with intersection of current types
      Set<SymTypeExpression> acceptedLubs = new HashSet<>();
      Set<SymTypeExpression> currentPotentialLubs;
      Set<SymTypeExpression> nextPotentialLubs = new HashSet<>();

      // unpack intersections
      typesSet_tmp = typeSet;
      typeSet = new HashSet<>();
      for (SymTypeExpression type : typesSet_tmp) {
        if (type.isUnionType()) {
          typeSet.addAll(((SymTypeOfUnion) type).getUnionizedTypeSet());
        }
        else {
          typeSet.add(type);
        }
      }

      // extract "arrayness"
      Set<Integer> arrayDims = new HashSet<>();
      if (typeSet.stream().allMatch(SymTypeExpression::isArrayType)) {
        typesSet_tmp = typeSet;
        typeSet = new HashSet<>();
        for (SymTypeExpression type : typesSet_tmp) {
          SymTypeArray array = (SymTypeArray) type;
          arrayDims.add(array.getDim());
          typeSet.add(array.getArgument());
        }
        // can only have lub if arrays have the same dimension
        if (arrayDims.size() == 1) {
          currentPotentialLubs = new HashSet<>(typeSet);
        }
        else {
          currentPotentialLubs = new HashSet<>();
        }
      }
      else if (typeSet.stream().noneMatch(SymTypeExpression::isArrayType)) {
        currentPotentialLubs = new HashSet<>(typeSet);
      }
      else {
        currentPotentialLubs = new HashSet<>();
      }

      // we have no unions, intersections or arrays on the top level
      // here the algorithm may be extended with regards to variables
      while (!currentPotentialLubs.isEmpty()) {
        for (SymTypeExpression type : typeSet) {
          for (Iterator<SymTypeExpression> i = currentPotentialLubs.iterator();
               i.hasNext(); ) {
            SymTypeExpression potentialLub = i.next();
            // if a type cannot be a subtype of a potential lub, it is not a lub
            if (!canBeSubTypeOf(type, potentialLub)) {
              i.remove();
              // however, the supertypes could still be lubs
              Collection<SymTypeExpression> superTypes =
                  potentialLub.getTypeInfo().getSuperTypesList();
              nextPotentialLubs.addAll(superTypes);
            }
          }
        }
        acceptedLubs.addAll(currentPotentialLubs);
        currentPotentialLubs = nextPotentialLubs;
        nextPotentialLubs = new HashSet<>();
      }
      // re-add "arrayness"
      if (arrayDims.size() == 1) {
        int arrayDim = arrayDims.stream().findFirst().get();
        acceptedLubs = acceptedLubs.stream()
            .map(t -> createTypeArray(t, arrayDim))
            .collect(Collectors.toSet());
      }

      // lub is the intersection of the calculated lubs (minus superclasses)
      // exception being the empty intersection -> no lub
      if (acceptedLubs.size() > 1) {
        lub = Optional.of(normalize(createIntersection(acceptedLubs)));
      }
      else if (acceptedLubs.size() == 1) {
        lub = acceptedLubs.stream().findFirst();
      }
      else {
        lub = Optional.of(createObscureType());
      }
    }

    return lub;
  }

  /**
   * calculates the one promoted type
   * ignoring the specifics of the context
   */
  public SymTypeExpression numericPromotion(SymTypeExpression... types) {
    return numericPromotion(List.of(types));
  }

  public SymTypeExpression numericPromotion(List<SymTypeExpression> types) {
    // according to the Java Specification (20)

    for (SymTypeExpression type : types) {
      if (!isNumericType(type)) {
        Log.error("0xFD285 internal error: tried to get"
            + "numeric promotion of non-numerics");
      }
    }

    for (SymTypeExpression type : types) {
      if (isDouble(type)) {
        return SymTypeExpressionFactory
            .createPrimitive(BasicSymbolsMill.DOUBLE);
      }
    }
    for (SymTypeExpression type : types) {
      if (isFloat(type)) {
        return SymTypeExpressionFactory
            .createPrimitive(BasicSymbolsMill.FLOAT);
      }
    }
    for (SymTypeExpression type : types) {
      if (isLong(type)) {
        return SymTypeExpressionFactory
            .createPrimitive(BasicSymbolsMill.LONG);
      }
    }

    // in arithmetic and array contexts, the promoted type is int
    // in a numeric choice context (e.g. a?2:4),
    // the result would not always be int
    // this has currently no relevance
    return SymTypeExpressionFactory
        .createPrimitive(BasicSymbolsMill.INT);
  }

  /**
   * test if the expression is of numeric type
   * (double, float, long, int, char, short, byte)
   */
  public boolean isNumericType(SymTypeExpression type) {
    return (isDouble(type) || isFloat(type) || isIntegralType(type));
  }

  /**
   * test if the expression is of integral type
   * (long, int, char, short, byte)
   */
  public boolean isIntegralType(SymTypeExpression type) {
    return (isLong(type) || isInt(type) || isChar(type) ||
        isShort(type) || isByte(type)
    );
  }

  public boolean isBoolean(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.BOOLEAN);
  }

  public boolean isInt(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.INT);
  }

  public boolean isDouble(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.DOUBLE);
  }

  public boolean isFloat(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.FLOAT);
  }

  public boolean isLong(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.LONG);
  }

  public boolean isChar(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.CHAR);
  }

  public boolean isShort(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.SHORT);
  }

  public boolean isByte(SymTypeExpression type) {
    SymTypeExpression unboxed = unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.BYTE);
  }

  public boolean isString(SymTypeExpression type) {
    // unboxed version of String is unlikely to be defined
    // as such we do not bother trying to unbox
    if (type.isObjectType()) {
      String fullName = type.printFullName();
      return fullName.equals("String") // unboxed
          || fullName.equals("java.lang.String"); // boxed
    }
    else {
      return false;
    }
  }

  // Helper

  protected SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

  protected SymTypeExpression normalize(SymTypeExpression type) {
    return normalizeVisitor.calculate(type);
  }

}

