// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.SymTypeRelations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

/**
 * checks for compatibility between SymTypes
 * delegate of SymTypeRelations
 */
public class SymTypeCompatibilityCalculator extends SymTypeRelations {

  @Override
  public boolean isCompatible(
      SymTypeExpression assignee,
      SymTypeExpression assigner) {
    boolean result;
    // null is compatible to any non-primitive type
    if (!assignee.isPrimitive() && assigner.isNullType()) {
      result = true;
    }
    // subtypes are assignable to their supertypes
    // additionaly, we allow unboxing
    else {
      SymTypeExpression unboxedAssignee = unbox(assignee);
      SymTypeExpression unboxedAssigner = unbox(assigner);
      result = internal_isSubTypeOf(unboxedAssigner, unboxedAssignee, true);
    }
    return result;
  }

  @Override
  public boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return internal_isSubTypeOf(subType, superType, false);
  }

  /**
   * isSubTypeOf and canBeSubTypeOf, as they are very similar
   * subTypeIsSoft if it is only a possibility, that it is a subtype
   */
  @Override
  protected boolean internal_isSubTypeOf(
      SymTypeExpression subType,
      SymTypeExpression superType,
      boolean subTypeIsSoft
  ) {
    SymTypeExpression normalizedSubType = normalize(subType);
    SymTypeExpression normalizedSuperType = normalize(superType);
    return internal_isSubTypeOfPreNormalized(
        normalizedSubType,
        normalizedSuperType,
        subTypeIsSoft
    );
  }

  @Override
  public boolean internal_isSubTypeOfPreNormalized(
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
    // arrays
    else if (superType.isArrayType() && subType.isArrayType()) {
      SymTypeArray superArray = (SymTypeArray) superType;
      SymTypeArray subArray = (SymTypeArray) subType;
      result = superArray.getDim() == subArray.getDim() &&
          internal_isSubTypeOfPreNormalized(
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
              internal_isSubTypeOfPreNormalized(
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

}
