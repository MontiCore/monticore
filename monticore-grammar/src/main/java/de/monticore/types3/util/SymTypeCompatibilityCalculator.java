// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

/**
 * checks for compatibility between SymTypes
 * delegate of SymTypeRelations
 */
public class SymTypeCompatibilityCalculator {

  protected SymTypeRelations symTypeRelations;

  public SymTypeCompatibilityCalculator(SymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
  }

  SymTypeRelations getSymTypeRelations() {
    return symTypeRelations;
  }

  public boolean isCompatible(
      SymTypeExpression assignee,
      SymTypeExpression assigner) {
    boolean result;
    // null is compatible to any non-primitive type
    if (!assignee.isPrimitive() && assigner.isNullType()) {
      result = true;
    }
    // subtypes are assignable to their supertypes
    // in addition, we allow boxing
    else {
      SymTypeExpression boxedAssignee =
          getSymTypeRelations().box(assignee);
      SymTypeExpression boxedAssigner =
          getSymTypeRelations().box(assigner);
      result = internal_isSubTypeOf(boxedAssigner, boxedAssignee, true);
    }
    return result;
  }

  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return internal_isSubTypeOf(subType, superType, false);
  }

  /**
   * isSubTypeOf and canBeSubTypeOf, as they are very similar
   * subTypeIsSoft if it is only a possibility, that it is a subtype
   */
  public boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    SymTypeExpression normalizedSubType =
        getSymTypeRelations().normalize(subType);
    SymTypeExpression normalizedSuperType =
        getSymTypeRelations().normalize(superType);
    return internal_isSubTypeOfPreNormalized(
        normalizedSubType,
        normalizedSuperType,
        subTypeIsSoft
    );
  }

  public boolean internal_isSubTypeOfPreNormalized(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    boolean result;
    // subType union -> all unionized types must be a subtype
    if (subType.isUnionType()) {
      result = true;
      for (SymTypeExpression subUType :
          ((SymTypeOfUnion) subType).getUnionizedTypeSet()) {
        result = result
            && internal_isSubTypeOfPreNormalized(subUType, superType, subTypeIsSoft);
      }
    }
    // supertype union -> must be a subtype of any unionized type
    else if (superType.isUnionType()) {
      result = false;
      for (SymTypeExpression superUType :
          ((SymTypeOfUnion) superType).getUnionizedTypeSet()) {
        result = result
            || internal_isSubTypeOfPreNormalized(subType, superUType, subTypeIsSoft);
      }
    }
    // supertype intersection -> must be a subtype of all intersected types
    else if (superType.isIntersectionType()) {
      result = true;
      for (SymTypeExpression superIType :
          ((SymTypeOfIntersection) superType).getIntersectedTypeSet()) {
        result = result
            && internal_isSubTypeOfPreNormalized(subType, superIType, subTypeIsSoft);
      }
    }
    // subType intersection -> any intersected type must be a subtype
    else if (subType.isIntersectionType()) {
      result = false;
      for (SymTypeExpression subIType :
          ((SymTypeOfIntersection) subType).getIntersectedTypeSet()) {
        result = result
            || internal_isSubTypeOfPreNormalized(subIType, superType, subTypeIsSoft);
      }
    }
    else {
      result = singleIsSubTypeOf(subType, superType, subTypeIsSoft);
    }
    return result;
  }

  // extension points
  // all extension points expect normalized types as input

  /**
   * this tests all symtypes that are not union or intersection types
   * (and don't contain them)
   */
  protected boolean singleIsSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    boolean result;
    // variable
    if (superType.isTypeVariable() || subType.isTypeVariable()) {
      result = typeVarIsSubTypeOf(subType, superType, subTypeIsSoft);
    }
    // arrays
    else if (superType.isArrayType() && subType.isArrayType()) {
      result = arrayIsSubTypeOf(
          (SymTypeArray) subType,
          (SymTypeArray) superType,
          subTypeIsSoft
      );
    }
    // unboxed primitives
    else if (superType.isPrimitive() && subType.isPrimitive()) {
      result = unboxedPrimitiveIsSubTypeOf(
          (SymTypePrimitive) subType,
          (SymTypePrimitive) superType,
          subTypeIsSoft
      );
    }
    // boxed primitives
    else if (
        !superType.isPrimitive() && !subType.isPrimitive() &&
            (getSymTypeRelations().isNumericType(superType) ||
                getSymTypeRelations().isBoolean(superType)) &&
            (getSymTypeRelations().isNumericType(subType) ||
                getSymTypeRelations().isBoolean(subType))
    ) {
      result = boxedPrimitiveIsSubTypeOf(
          (SymTypeOfObject) subType,
          (SymTypeOfObject) superType,
          subTypeIsSoft
      );
    }
    // functions
    else if (superType.isFunctionType() && subType.isFunctionType()) {
      result = functionIsSubTypeOf(
          (SymTypeOfFunction) subType,
          (SymTypeOfFunction) superType,
          subTypeIsSoft
      );
    }
    // objects
    else if (
        (superType.isObjectType() || superType.isGenericType()) &&
            (subType.isObjectType() || subType.isGenericType())) {
      result = objectIsSubTypeOf(subType, superType, subTypeIsSoft);
    }
    else {
      result = false;
    }
    return result;
  }

  /**
   * whether one expression is the subType of another,
   * with at least one of the types being a variable
   */
  protected boolean typeVarIsSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    // this is not enough for full generics
    boolean result;
    // T is compatible to T
    // this has to be checked specifically,
    // as two unbounded type variable are not subTypes of each other otherwise
    if (subType.isTypeVariable() &&
        superType.isTypeVariable() &&
        subType.deepEquals(superType)
    ) {
      result = true;
    }
    // T extends B is a subType of A iff B is a subType of A.
    else if (subType.isTypeVariable()) {
      SymTypeVariable subVar = (SymTypeVariable) subType;
      SymTypeExpression normalizedUpperBound =
          getSymTypeRelations().normalize(subVar.getUpperBound());
      result = internal_isSubTypeOfPreNormalized(
          normalizedUpperBound, superType, subTypeIsSoft);
    }
    // T super A is a superType of B iff A is a superType of B.
    // This example cannot be (directly) represented using Java,
    // but can occur while type checking Java (s. Wild FJ (2005))
    else if (superType.isTypeVariable()) {
      SymTypeVariable superVar = (SymTypeVariable) superType;
      SymTypeExpression normalizedLowerBound =
          getSymTypeRelations().normalize(superVar.getLowerBound());
      result = internal_isSubTypeOfPreNormalized(
          subType, normalizedLowerBound, subTypeIsSoft);
    }
    else {
      Log.error("0xFDB32 internal error, "
          + "expected an type variable");
      result = false;
    }
    return result;
  }

  /**
   * whether one array is the subType of another
   */
  protected boolean arrayIsSubTypeOf(
      SymTypeArray subArray,
      SymTypeArray superArray,
      boolean subTypeIsSoft
  ) {
    return superArray.getDim() == subArray.getDim() &&
        internal_isSubTypeOfPreNormalized(
            superArray.getArgument(),
            subArray.getArgument(),
            subTypeIsSoft
        );
  }

  /**
   * whether unboxed primitives are in a subType relation
   * s. Java spec 20 4.10.1
   */
  protected boolean unboxedPrimitiveIsSubTypeOf(
      SymTypePrimitive subType,
      SymTypePrimitive superType,
      boolean subTypeIsSoft
  ) {
    return primitiveIsSubTypeOf(subType, superType, subTypeIsSoft);
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
  protected boolean boxedPrimitiveIsSubTypeOf(
      SymTypeOfObject subType,
      SymTypeOfObject superType,
      boolean subTypeIsSoft
  ) {
    return primitiveIsSubTypeOf(subType, superType, subTypeIsSoft);
  }

  protected boolean primitiveIsSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    boolean result;
    if (getSymTypeRelations().isBoolean(superType) &&
        getSymTypeRelations().isBoolean(subType)) {
      result = true;
    }
    else if (getSymTypeRelations().isNumericType(superType) &&
        getSymTypeRelations().isNumericType(subType)) {
      if (getSymTypeRelations().isDouble(superType)) {
        result = true;
      }
      else if (getSymTypeRelations().isFloat(superType) &&
          (getSymTypeRelations().isFloat(subType) ||
              getSymTypeRelations().isIntegralType(subType)
          )) {
        result = true;
      }
      else if (getSymTypeRelations().isLong(superType) &&
          getSymTypeRelations().isIntegralType(
              subType)) {
        result = true;
      }
      else if (getSymTypeRelations().isInt(superType) &&
          getSymTypeRelations().isIntegralType(subType) &&
          !getSymTypeRelations().isLong(subType)) {
        result = true;
      }
      else if (getSymTypeRelations().isChar(superType) &&
          getSymTypeRelations().isChar(subType)) {
        result = true;
      }
      else if (getSymTypeRelations().isShort(superType) &&
          (getSymTypeRelations().isShort(subType)
              || getSymTypeRelations().isByte(subType))) {
        result = true;
      }
      else if (getSymTypeRelations().isByte(superType) &&
          getSymTypeRelations().isByte(subType)) {
        result = true;
      }
      else {
        result = false;
      }
    }
    else {
      result = false;
    }
    return result;
  }

  protected boolean functionIsSubTypeOf(
      SymTypeOfFunction subFunc,
      SymTypeOfFunction superFunc,
      boolean subTypeIsSoft
  ) {
    boolean result = true;
    // return type
    result = result &&
        internal_isSubTypeOfPreNormalized(superFunc.getType(), subFunc.getType(), subTypeIsSoft);
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
      result = result && internal_isSubTypeOfPreNormalized(
          subParamType, superParamType, subTypeIsSoft
      );
    }
    return result;
  }

  /**
   * whether one object is a subType of another
   * this includes SymTypeOfObject, as well as SymTypeOfGenerics
   */
  protected boolean objectIsSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    boolean result;
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
            // here we box the symTypes, as we did not before
            // this leads to List<int> being a subType of java.util.List<Integer>
            // as Lists of primitives are not available in Java,
            // they are interpreted as being identical.
            internal_isSubTypeOfPreNormalized(
                getSymTypeRelations().normalize(
                    getSymTypeRelations().box(subSuperExpr)
                ),
                getSymTypeRelations().box(superType),
                subTypeIsSoft
            );
      }
    }
    return result;
  }

}
