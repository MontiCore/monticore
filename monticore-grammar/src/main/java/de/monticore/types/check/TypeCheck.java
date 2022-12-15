/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

/**
 * Deprecated due to static methods
 * use {@link ITypeRelations} instead,
 * preferably provided by {@link TypeCalculator}
 */
@Deprecated
public class TypeCheck {
  
  /**
   * only used to provide deprecated static methods
   */
  protected static TypeRelations typeRelations = new TypeRelations();

  public static boolean compatible(SymTypeExpression left,
                                   SymTypeExpression right) {
    return typeRelations.compatible(left, right);
  }

  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    return typeRelations.isSubtypeOf(subType, superType);
  }

  protected static int calculateInheritanceDistance(SymTypeExpression specific, SymTypeExpression general){
    return typeRelations.calculateInheritanceDistance(specific, general);
  }

  public static int calculateInheritanceDistance(SymTypePrimitive specific, SymTypePrimitive general) {
    return typeRelations.calculateInheritanceDistance(specific, general);
  }

  public static boolean isBoolean(SymTypeExpression type) {
    return typeRelations.isBoolean(type);
  }

  public static boolean isInt(SymTypeExpression type) {
    return typeRelations.isInt(type);
  }

  public static boolean isDouble(SymTypeExpression type) {
    return typeRelations.isDouble(type);
  }

  public static boolean isFloat(SymTypeExpression type) {
    return typeRelations.isFloat(type);
  }

  public static boolean isLong(SymTypeExpression type) {
    return typeRelations.isLong(type);
  }

  public static boolean isChar(SymTypeExpression type) {
    return typeRelations.isChar(type);
  }

  public static boolean isShort(SymTypeExpression type) {
    return typeRelations.isShort(type);
  }

  public static boolean isByte(SymTypeExpression type) {
    return typeRelations.isByte(type);
  }

  public static boolean isVoid(SymTypeExpression type) {
    return typeRelations.isVoid(type);
  }

  public static boolean isString(SymTypeExpression type) {
    return typeRelations.isString(type);
  }
}

