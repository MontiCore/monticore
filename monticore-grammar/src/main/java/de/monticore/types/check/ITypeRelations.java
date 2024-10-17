/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

/**
 * This class is intended to provide typeChecking functionality
 * with regard to relations of types
 */
public interface ITypeRelations {

  /**
   * Function 3:
   * Given two SymTypeExpressions super, sub:
   * This function answers, whether the right type is a subtype of the left type in an assignment.
   * (This allows to store/use values of type "sub" at all positions of type "super".
   * Compatibility examples:
   * compatible("int", "long")       (in all directions)
   * compatible("long", "int")       (in all directions)
   * compatible("double", "float")   (in all directions)
   * compatible("Person", "Student") (uni-directional)
   * <p>
   * Incompatible:
   * !compatible("double", "int")   (in all directions)
   * <p>
   * The concrete Typechecker has to decide on further issues, like
   * !compatible("List<double>", "List<int>")
   * where e.g. Java and OCL/P differ in their answers
   *
   * @param left  Super-Type
   * @param right Sub-Type (assignment-compatible to supertype?)
   * <p>
   */
  boolean compatible(SymTypeExpression left, SymTypeExpression right);

  /**
   * determines if one SymTypeExpression is a subtype of another SymTypeExpression
   *
   * @param subType   the SymTypeExpression that could be a subtype of the other SymTypeExpression
   * @param superType the SymTypeExpression that could be a supertype of the other SymTypeExpression
   */
  boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType);

  /**
   * calculate the minimum inheritance distance from the specific type to the general type
   * e.g. C extends B extends A => object of type C has distance of 2 to object of type A
   * object of type B has distance of 1 to object of type A
   * object of type A has distance of 0 to object of type A
   *
   * @param specific the specific type
   * @param general  the general type
   * @return 0 if they are the same type, else their minimum inheritance distance
   */
  int calculateInheritanceDistance(SymTypeExpression specific, SymTypeExpression general);

  int calculateInheritanceDistance(SymTypePrimitive specific, SymTypePrimitive general);

  boolean isBoolean(SymTypeExpression type);

  boolean isInt(SymTypeExpression type);

  boolean isDouble(SymTypeExpression type);

  boolean isFloat(SymTypeExpression type);

  boolean isLong(SymTypeExpression type);

  boolean isChar(SymTypeExpression type);

  boolean isShort(SymTypeExpression type);

  boolean isByte(SymTypeExpression type);

  boolean isVoid(SymTypeExpression type);

  boolean isString(SymTypeExpression type);

  boolean isNumericType(SymTypeExpression type);

  boolean isIntegralType(SymTypeExpression type);
}
