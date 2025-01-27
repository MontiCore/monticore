/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

/**
 * @deprecated due to static methods
 * use {@link ITypeRelations} instead,
 * preferably provided by {@link TypeCalculator}
 */
@Deprecated(forRemoval = true)
public class TypeCheck {
  
  /**
   * only used to provide deprecated static methods
   */
  protected static TypeRelations typeRelations = new TypeRelations();

  @Deprecated
  public static boolean compatible(SymTypeExpression left,
                                   SymTypeExpression right) {
    return typeRelations.compatible(left, right);
  }

  @Deprecated
  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    return typeRelations.isSubtypeOf(subType, superType);
  }

  @Deprecated
  protected static int calculateInheritanceDistance(SymTypeExpression specific, SymTypeExpression general){
    return typeRelations.calculateInheritanceDistance(specific, general);
  }

  @Deprecated
  public static int calculateInheritanceDistance(SymTypePrimitive specific, SymTypePrimitive general) {
    return typeRelations.calculateInheritanceDistance(specific, general);
  }

  @Deprecated
  public static boolean isBoolean(SymTypeExpression type) {
    return typeRelations.isBoolean(type);
  }

  @Deprecated
  public static boolean isInt(SymTypeExpression type) {
    return typeRelations.isInt(type);
  }

  @Deprecated
  public static boolean isDouble(SymTypeExpression type) {
    return typeRelations.isDouble(type);
  }

  @Deprecated
  public static boolean isFloat(SymTypeExpression type) {
    return typeRelations.isFloat(type);
  }

  @Deprecated
  public static boolean isLong(SymTypeExpression type) {
    return typeRelations.isLong(type);
  }

  @Deprecated
  public static boolean isChar(SymTypeExpression type) {
    return typeRelations.isChar(type);
  }

  @Deprecated
  public static boolean isShort(SymTypeExpression type) {
    return typeRelations.isShort(type);
  }

  @Deprecated
  public static boolean isByte(SymTypeExpression type) {
    return typeRelations.isByte(type);
  }

  @Deprecated
  public static boolean isVoid(SymTypeExpression type) {
    return typeRelations.isVoid(type);
  }

  @Deprecated
  public static boolean isString(SymTypeExpression type) {
    return typeRelations.isString(type);
  }
}

