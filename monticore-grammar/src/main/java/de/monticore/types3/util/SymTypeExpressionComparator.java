package de.monticore.types3.util;

import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.se_rwth.commons.logging.Log;

import java.util.Comparator;

/**
 * A Helper to create (Tree)Maps.
 * <p>
 * Mostly, this tries to avoid costly comparisons which require printing.
 */
public class SymTypeExpressionComparator
    implements Comparator<SymTypeExpression> {

  @Override
  public int compare(SymTypeExpression o1, SymTypeExpression o2) {
    Log.errorIfNull(o1);
    Log.errorIfNull(o2);
    if (o1.deepEquals(o2)) {
      return 0;
    }
    else if (o1.isUnionType()) {
      if (!o2.isUnionType()) {
        return -1;
      }
      else {
        return compareUnion(o1.asUnionType(), o2.asUnionType());
      }
    }
    else if (o2.isUnionType()) {
      return 1;
    }
    else if (o1.isIntersectionType()) {
      if (!o2.isIntersectionType()) {
        return -1;
      }
      else {
        return compareIntersection(
            o1.asIntersectionType(),
            o2.asIntersectionType()
        );
      }
    }
    else if (o2.isIntersectionType()) {
      return 1;
    }
    else if (o1.isTupleType()) {
      if (!o2.isTupleType()) {
        return -1;
      }
      else {
        return compareTuple(o1.asTupleType(), o2.asTupleType());
      }
    }
    else if (o2.isTupleType()) {
      return 1;
    }
    else if (o1.isFunctionType()) {
      if (!o2.isFunctionType()) {
        return -1;
      }
      else {
        return compareFunction(o1.asFunctionType(), o2.asFunctionType());
      }
    }
    else if (o2.isFunctionType()) {
      return 1;
    }
    else if (o1.isArrayType()) {
      if (!o2.isArrayType()) {
        return -1;
      }
      else {
        return compareArray(o1.asArrayType(), o2.asArrayType());
      }
    }
    else if (o2.isArrayType()) {
      return 1;
    }
    else if (o1.isPrimitive()) {
      if (!o2.isPrimitive()) {
        return -1;
      }
      else {
        return comparePrimitive(o1.asPrimitive(), o2.asPrimitive());
      }
    }
    else if (o2.isPrimitive()) {
      return 1;
    }
    else if (o1.isSIUnitType()) {
      if (!o2.isSIUnitType()) {
        return -1;
      }
      else {
        return compareSIUnit(o1.asSIUnitType(), o2.asSIUnitType());
      }
    }
    else if (o2.isSIUnitType()) {
      return 1;
    }
    else if (o1.isNumericWithSIUnitType()) {
      if (!o2.isNumericWithSIUnitType()) {
        return -1;
      }
      else {
        return compareNumericWithSIUnit(
            o1.asNumericWithSIUnitType(),
            o2.asNumericWithSIUnitType()
        );
      }
    }
    else if (o2.isNumericWithSIUnitType()) {
      return 1;
    }
    else if (o1.isRegExType()) {
      if (!o2.isRegExType()) {
        return -1;
      }
      else {
        return compareRegEx(o1.asRegExType(), o2.asRegExType());
      }
    }
    else if (o2.isRegExType()) {
      return 1;
    }
    else if (o1.isObjectType()) {
      if (!o2.isObjectType()) {
        return -1;
      }
      else {
        return compareObject(o1.asObjectType(), o2.asObjectType());
      }
    }
    else if (o2.isObjectType()) {
      return 1;
    }
    else if (o1.isGenericType()) {
      if (!o2.isGenericType()) {
        return -1;
      }
      else {
        return compareGeneric(o1.asGenericType(), o2.asGenericType());
      }
    }
    else if (o2.isGenericType()) {
      return 1;
    }
    else if (o1.isTypeVariable()) {
      if (!o2.isTypeVariable()) {
        return -1;
      }
      else {
        return compareTypeVar(o1.asTypeVariable(), o2.asTypeVariable());
      }
    }
    else if (o2.isTypeVariable()) {
      return 1;
    }
    else if (o1.isWildcard()) {
      if (!o2.isWildcard()) {
        return -1;
      }
      else {
        return compareWildcard(o1.asWildcard(), o2.asWildcard());
      }
    }
    else if (o2.isWildcard()) {
      return 1;
    }
    // the following do not have any further comparisons,
    // as two of the same type cannot differ
    else if (o1.isNullType()) {
      return -1;
    }
    else if (o2.isNullType()) {
      return 1;
    }
    else if (o1.isVoidType()) {
      return -1;
    }
    else if (o2.isVoidType()) {
      return 1;
    }
    else if (o1.isObscureType()) {
      return -1;
    }
    return logUnimplemented();
  }

  protected int compareUnion(SymTypeOfUnion o1, SymTypeOfUnion o2) {
    if (o1.sizeUnionizedTypes() < o2.sizeUnionizedTypes()) {
      return -1;
    }
    else if (o1.sizeUnionizedTypes() > o2.sizeUnionizedTypes()) {
      return 1;
    }
    else {
      return o1.printFullName().compareTo(o2.printFullName());
    }
  }

  protected int compareIntersection(SymTypeOfIntersection o1, SymTypeOfIntersection o2) {
    if (o1.sizeIntersectedTypes() < o2.sizeIntersectedTypes()) {
      return -1;
    }
    else if (o1.sizeIntersectedTypes() > o2.sizeIntersectedTypes()) {
      return 1;
    }
    else {
      return o1.printFullName().compareTo(o2.printFullName());
    }
  }

  protected int compareTuple(SymTypeOfTuple o1, SymTypeOfTuple o2) {
    if (o1.sizeTypes() < o2.sizeTypes()) {
      return -1;
    }
    else if (o1.sizeTypes() > o2.sizeTypes()) {
      return 1;
    }
    else {
      int res;
      for (int i = 0; i < o1.sizeTypes(); i++) {
        res = compare(o1.getType(i), o2.getType(i));
        if (res != 0) {
          return res;
        }
      }
    }
    return logUnimplemented();
  }

  protected int compareFunction(SymTypeOfFunction o1, SymTypeOfFunction o2) {
    if (o1.sizeArgumentTypes() < o2.sizeArgumentTypes()) {
      return -1;
    }
    else if (o1.sizeArgumentTypes() > o2.sizeArgumentTypes()) {
      return 1;
    }
    else if (o1.isElliptic() && !o2.isElliptic()) {
      return -1;
    }
    else if (!o1.isElliptic() && o2.isElliptic()) {
      return 1;
    }
    else {
      int res = compare(o1.getType(), o2.getType());
      if (res != 0) {
        return res;
      }
      for (int i = 0; i < o1.sizeArgumentTypes(); i++) {
        res = compare(o1.getArgumentType(i), o2.getArgumentType(i));
        if (res != 0) {
          return res;
        }
      }
    }
    return logUnimplemented();
  }

  protected int compareArray(SymTypeArray o1, SymTypeArray o2) {
    if (o1.getDim() > o2.getDim()) {
      return -1;
    }
    else if (o1.getDim() < o2.getDim()) {
      return 1;
    }
    return compare(o1.getArgument(), o2.getArgument());
  }

  protected int comparePrimitive(SymTypePrimitive o1, SymTypePrimitive o2) {
    return compareSymbol(o1.getTypeInfo(), o2.getTypeInfo());
  }

  protected int compareSIUnit(SymTypeOfSIUnit o1, SymTypeOfSIUnit o2) {
    return o1.printFullName().compareTo(o2.printFullName());
  }

  protected int compareNumericWithSIUnit(SymTypeOfNumericWithSIUnit o1, SymTypeOfNumericWithSIUnit o2) {
    int res = compare(o1.getNumericType(), o2.getNumericType());
    if (res != 0) {
      return res;
    }
    return compareSIUnit(o1.getSIUnitType(), o2.getSIUnitType());
  }

  protected int compareRegEx(SymTypeOfRegEx o1, SymTypeOfRegEx o2) {
    return o1.getRegExString().compareTo(o2.getRegExString());
  }

  protected int compareObject(SymTypeOfObject o1, SymTypeOfObject o2) {
    return compareSymbol(o1.getTypeInfo(), o2.getTypeInfo());
  }

  protected int compareGeneric(SymTypeOfGenerics o1, SymTypeOfGenerics o2) {
    if (o1.sizeArguments() > o2.sizeArguments()) {
      return -1;
    }
    else if (o1.sizeArguments() < o2.sizeArguments()) {
      return 1;
    }
    int res;
    for (int i = 0; i < o1.sizeArguments(); i++) {
      res = compare(o1.getArgument(i), o2.getArgument(i));
      if (res != 0) {
        return res;
      }
    }
    return compareSymbol(o1.getTypeInfo(), o2.getTypeInfo());
  }

  protected int compareTypeVar(SymTypeVariable o1, SymTypeVariable o2) {
    if (o1.hasTypeVarSymbol() && !o2.hasTypeVarSymbol()) {
      return -1;
    }
    else if (!o1.hasTypeVarSymbol() && o2.hasTypeVarSymbol()) {
      return 1;
    }
    int res = compare(o1.getUpperBound(), o2.getUpperBound());
    if (res != 0) {
      return res;
    }
    res = compare(o1.getLowerBound(), o2.getLowerBound());
    if (res != 0) {
      return res;
    }
    else if (!o1.hasTypeVarSymbol() && !o2.hasTypeVarSymbol()) {
      return o1.printFullName().compareTo(o2.printFullName());
    }
    else {
      return compareSymbol(o1.getTypeVarSymbol(), o2.getTypeVarSymbol());
    }
  }

  protected int compareWildcard(SymTypeOfWildcard o1, SymTypeOfWildcard o2) {
    if (o1.hasBound() && !o2.hasBound()) {
      return -1;
    }
    else if (!o1.hasBound() && !o2.hasBound()) {
      return 1;
    }
    else if (o1.isUpper() && !o2.isUpper()) {
      return -1;
    }
    else if (!o1.isUpper() && o2.isUpper()) {
      return 1;
    }
    return compare(o1.getBound(), o2.getBound());
  }

  protected int compareSymbol(ISymbol o1, ISymbol o2) {
    return o1.getFullName().compareTo(o2.getFullName());
  }

  // Helper

  /**
   * Logs an error and returns default comparison value;
   * This is not expected to be ever called.
   */
  protected int logUnimplemented() {
    Log.error("0xFD445 internal error: unimplemented comparison.");
    return 0;
  }

}
