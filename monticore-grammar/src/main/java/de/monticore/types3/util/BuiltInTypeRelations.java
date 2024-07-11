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

  public BuiltInTypeRelations() {
  }

  /**
   * @deprecated use constructor {@link #BuiltInTypeRelations()}
   */
  @Deprecated(forRemoval = true)
  public BuiltInTypeRelations(SymTypeRelations symTypeRelations) {
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
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.BOOLEAN);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Boolean");
    }
    return false;
  }

  public boolean isInt(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.INT);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Integer");
    }
    return false;
  }

  public boolean isDouble(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.DOUBLE);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Double");
    }
    return false;
  }

  public boolean isFloat(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.FLOAT);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Float");
    }
    return false;
  }

  public boolean isLong(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.LONG);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Long");
    }
    return false;
  }

  public boolean isChar(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.CHAR);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Character");
    }
    return false;
  }

  public boolean isShort(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.SHORT);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Short");
    }
    return false;
  }

  public boolean isByte(SymTypeExpression type) {
    if (type.isPrimitive()) {
      return type.printFullName().equals(BasicSymbolsMill.BYTE);
    }
    if (type.isObjectType()) {
      return type.printFullName().equals("java.lang.Byte");
    }
    return false;
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

  public boolean isTop(SymTypeExpression type) {
    return SymTypeRelations.isSubTypeOf(
        SymTypeExpressionFactory.createTopType(),
        type
    );
  }

  public boolean isBottom(SymTypeExpression type) {
    return SymTypeRelations.isSubTypeOf(
        type,
        SymTypeExpressionFactory.createBottomType()
    );
  }

}
