// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * relations for built-in SymTypes
 * these are the primitives, boxed primitives and String
 * delegate of SymTypeRelations
 */
public class BuiltInTypeRelations {

  protected SymTypeRelations symTypeRelations;

  public BuiltInTypeRelations(SymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
  }

  SymTypeRelations getSymTypeRelations() {
    return symTypeRelations;
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

  public boolean isNumericType(SymTypeExpression type) {
    return (isDouble(type) || isFloat(type) || isIntegralType(type));
  }

  public boolean isIntegralType(SymTypeExpression type) {
    return (isLong(type) || isInt(type) || isChar(type) ||
        isShort(type) || isByte(type)
    );
  }

  public boolean isBoolean(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.BOOLEAN);
  }

  public boolean isInt(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.INT);
  }

  public boolean isDouble(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.DOUBLE);
  }

  public boolean isFloat(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.FLOAT);
  }

  public boolean isLong(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.LONG);
  }

  public boolean isChar(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.CHAR);
  }

  public boolean isShort(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
    return unboxed.isPrimitive() &&
        unboxed.getTypeInfo().getName().equals(BasicSymbolsMill.SHORT);
  }

  public boolean isByte(SymTypeExpression type) {
    SymTypeExpression unboxed = getSymTypeRelations().unbox(type);
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

}