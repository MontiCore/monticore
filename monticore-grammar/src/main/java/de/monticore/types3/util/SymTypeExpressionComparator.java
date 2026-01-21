package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Helper to create (Tree)Maps.
 * <p>
 * Mostly, this tries to avoid costly comparisons which require printing.
 */
public class SymTypeExpressionComparator
    implements Comparator<SymTypeExpression> {

  protected static SymTypeExpressionComparator delegate;

  public static int compareSymTypeExpressions(
      SymTypeExpression o1,
      SymTypeExpression o2
  ) {
    return getDelegate().compare(o1, o2);
  }

  @Override
  public int compare(SymTypeExpression o1, SymTypeExpression o2) {
    Preconditions.checkNotNull(o1);
    Preconditions.checkNotNull(o2);

    int res;

    int orderOfSubType1 = getOrderOfSubType(o1);
    int orderOfSubType2 = getOrderOfSubType(o2);
    int subTypeOrdering = Integer.compare(orderOfSubType1, orderOfSubType2);

    if (subTypeOrdering != 0) {
      res = subTypeOrdering;
    }
    else if (o1.deepEquals(o2)) {
      res = 0;
    }
    else if (o1.isUnionType() && o2.isUnionType()) {
      res = compareUnion(o1.asUnionType(), o2.asUnionType());
    }
    else if (o1.isIntersectionType() && o2.isIntersectionType()) {
      res = compareIntersection(o1.asIntersectionType(), o2.asIntersectionType());
    }
    else if (o1.isTupleType() && o2.isTupleType()) {
      res = compareTuple(o1.asTupleType(), o2.asTupleType());
    }
    else if (o1.isFunctionType() && o2.isFunctionType()) {
      res = compareFunction(o1.asFunctionType(), o2.asFunctionType());
    }
    else if (o1.isArrayType() && o2.isArrayType()) {
      res = compareArray(o1.asArrayType(), o2.asArrayType());
    }
    else if (o1.isPrimitive() && o2.isPrimitive()) {
      res = comparePrimitive(o1.asPrimitive(), o2.asPrimitive());
    }
    else if (o1.isSIUnitType() && o2.isSIUnitType()) {
      res = compareSIUnit(o1.asSIUnitType(), o2.asSIUnitType());
    }
    else if (o1.isNumericWithSIUnitType() && o2.isNumericWithSIUnitType()) {
      res = compareNumericWithSIUnit(
          o1.asNumericWithSIUnitType(),
          o2.asNumericWithSIUnitType()
      );
    }
    else if (o1.isRegExType() && o2.isRegExType()) {
      res = compareRegEx(o1.asRegExType(), o2.asRegExType());
    }
    else if (o1.isObjectType() && o2.isObjectType()) {
      res = compareObject(o1.asObjectType(), o2.asObjectType());
    }
    else if (o1.isGenericType() && o2.isGenericType()) {
      res = compareGeneric(o1.asGenericType(), o2.asGenericType());
    }
    else if (o1.isWildcard() && o2.isWildcard()) {
      res = compareWildcard(o1.asWildcard(), o2.asWildcard());
    }
    else if (o1.isTypeVariable() && o2.isTypeVariable()) {
      res = compareTypeVar(o1.asTypeVariable(), o2.asTypeVariable());
    }
    else if (o1.isInferenceVariable() && o2.isInferenceVariable()) {
      res = compareInfVar(o1.asInferenceVariable(), o2.asInferenceVariable());
    }
    // null, void, and obscure are handled by deepEquals
    else {
      throwUnimplemented();
      res = -41;
    }
    return res;
  }

  protected int getOrderOfSubType(SymTypeExpression type) {
    if (type.isObscureType()) {
      return 0;
    }
    else if (type.isVoidType()) {
      return 10;
    }
    else if (type.isNullType()) {
      return 20;
    }
    else if (type.isPrimitive()) {
      return 30;
    }
    else if (type.isSIUnitType()) {
      return 40;
    }
    else if (type.isNumericWithSIUnitType()) {
      return 50;
    }
    else if (type.isRegExType()) {
      return 55;
    }
    else if (type.isObjectType()) {
      return 60;
    }
    else if (type.isGenericType()) {
      return 70;
    }
    else if (type.isArrayType()) {
      return 80;
    }
    else if (type.isFunctionType()) {
      return 90;
    }
    else if (type.isTupleType()) {
      return 100;
    }
    else if (type.isUnionType()) {
      return 110;
    }
    else if (type.isIntersectionType()) {
      return 120;
    }
    else if (type.isTypeVariable()) {
      return 130;
    }
    else if (type.isInferenceVariable()) {
      return 140;
    }
    else if (type.isWildcard()) {
      return 150;
    }
    else {
      throwUnimplemented();
      return -42;
    }
  }

  protected int compareUnion(SymTypeOfUnion o1, SymTypeOfUnion o2) {
    return compareSets(o1.getUnionizedTypeSet(), o2.getUnionizedTypeSet());
  }

  protected int compareIntersection(SymTypeOfIntersection o1, SymTypeOfIntersection o2) {
    return compareSets(o1.getIntersectedTypeSet(), o2.getIntersectedTypeSet());
  }

  protected int compareTuple(SymTypeOfTuple o1, SymTypeOfTuple o2) {
    return compareLists(o1.getTypeList(), o2.getTypeList());
  }

  protected int compareFunction(SymTypeOfFunction o1, SymTypeOfFunction o2) {
    int retTypeComp = compareSymTypeExpressions(o1.getType(), o2.getType());
    if (retTypeComp != 0) {
      return retTypeComp;
    }
    int argsComp = compareLists(o1.getArgumentTypeList(), o2.getArgumentTypeList());
    if (argsComp != 0) {
      return argsComp;
    }
    else if (o1.isElliptic() && !o2.isElliptic()) {
      return -1;
    }
    else if (!o1.isElliptic() && o2.isElliptic()) {
      return 1;
    }
    throwUnimplemented();
    return -42;
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

  protected int compareNumericWithSIUnit(
      SymTypeOfNumericWithSIUnit o1,
      SymTypeOfNumericWithSIUnit o2
  ) {
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
    int symComp = compareSymbol(o1.getTypeInfo(), o2.getTypeInfo());
    if (symComp != 0) {
      return symComp;
    }
    return compareLists(o1.getArgumentList(), o2.getArgumentList());
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

  protected int compareTypeVar(SymTypeVariable o1, SymTypeVariable o2) {
    return compareSymbol(o1.getTypeVarSymbol(), o2.getTypeVarSymbol());
  }

  protected int compareInfVar(
      SymTypeInferenceVariable o1,
      SymTypeInferenceVariable o2
  ) {
    int idComp = Integer.compare(o1._internal_getID(), o2._internal_getID());
    if (idComp != 0) {
      return idComp;
    }
    int upperComp = compare(o1.getUpperBound(), o2.getUpperBound());
    if (upperComp != 0) {
      return upperComp;
    }
    int lowerComp = compare(o1.getLowerBound(), o2.getLowerBound());
    if (lowerComp != 0) {
      return lowerComp;
    }
    throwUnimplemented();
    return -42;
  }

  protected int compareSymbol(ISymbol o1, ISymbol o2) {
    int res;
    // note: this case is only to speed comparisons up
    if (o1 == o2 || o1.equals(o2)) {
      res = 0;
    }
    else {
      int resName = o1.getFullName().compareTo(o2.getFullName());
      if (resName != 0) {
        res = resName;
      }
      // this is, in most cases, not supposed to happen,
      // as functions are differentiated by their types,
      // and otherwise, at this position, we would have two distinct symbols
      // with the same kind and name.
      // This only happens in specific situations, e.g.,
      // creating a broken symbol table and continuing to check CoCos
      // to find further potential issus with the model (s, e.g., MontiArc).
      else {
        int resPos = o1.getSourcePosition().compareTo(o2.getSourcePosition());
        if (resPos != 0) {
          res = resPos;
        }
        else {
          Log.warn("0xFD738 internal warning: "
              + "found two symbols with the same name \""
              + o1.getFullName() + "\" and the same source position "
              + o1.getSourcePosition().toStringFullPath()
              + ", which most likely indicates "
              + "a wrong symbol table generation."
              + " Alternatively, the symbol comparison implementation "
              + "needs to be extended (this is unexpected)."
          );
          // just assume that they are identical for now
          res = 0;
        }
      }
    }
    return res;
  }

  // Helper

  protected <T extends Comparable<T>> int compareLists(
      List<? extends T> o1,
      List<? extends T> o2
  ) {
    if (o1.size() < o2.size()) {
      return -1;
    }
    else if (o1.size() > o2.size()) {
      return 1;
    }
    else {
      for (int i = 0; i < o1.size(); i++) {
        T e1 = o1.get(i);
        T e2 = o2.get(i);
        int compI = e1.compareTo(e2);
        if (compI != 0) {
          return compI;
        }
      }
      return 0;
    }
  }

  protected <T extends Comparable<T>> int compareSets(
      Set<? extends T> o1,
      Set<? extends T> o2
  ) {
    if (o1.size() < o2.size()) {
      return -1;
    }
    else if (o1.size() > o2.size()) {
      return 1;
    }
    else {
      List<T> sorted1 = o1.stream().sorted().collect(Collectors.toList());
      List<T> sorted2 = o2.stream().sorted().collect(Collectors.toList());
      return compareLists(sorted1, sorted2);
    }
  }

  /**
   * This is not expected to be ever called.
   */
  protected void throwUnimplemented() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "0xFD445 unimplemented comparison."
    );
  }

  // static delegate

  public static void init() {
    Log.trace("init default SymTypeExpressionComparator", "TypeCheck setup");
    setDelegate(new SymTypeExpressionComparator());
  }

  public static void reset() {
    SymTypeExpressionComparator.delegate = null;
  }

  protected static void setDelegate(SymTypeExpressionComparator newDelegate) {
    SymTypeExpressionComparator.delegate =
        Preconditions.checkNotNull(newDelegate);
  }

  protected static SymTypeExpressionComparator getDelegate() {
    if (SymTypeExpressionComparator.delegate == null) {
      init();
    }
    return SymTypeExpressionComparator.delegate;
  }

}
