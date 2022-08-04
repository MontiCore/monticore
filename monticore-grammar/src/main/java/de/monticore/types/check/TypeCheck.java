/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import static de.monticore.types.check.SymTypePrimitive.unbox;

/**
 * This class is intended to provide typeChecking functionality.
 * It is designed as functional class (no state), allowing to
 * plug-in the appropriate implementation through subclasses,
 * Those subclasses can deal with variants of Expression, Literal
 * and Type-classes that are used in the respective project.
 * (It is thus configure along three dimensions:
 *    Literals
 *    Expressions
 *    Types)
 * This class only knows about the thre top Level grammars:
 * MCBasicTypes, ExpressionsBasis and MCLiteralsBasis, because it includes their
 * main NonTerminals in the signature.
 */
public class TypeCheck {
  
  /**
   * Function 3:
   * Given two SymTypeExpressions super, sub:
   * This function answers, whether the right type is a subtype of the left type in an assignment.
   * (This allows to store/use values of type "sub" at all positions of type "super".
   * Compatibility examples:
   *      compatible("int", "long")       (in all directions)
   *      compatible("long", "int")       (in all directions)
   *      compatible("double", "float")   (in all directions)
   *      compatible("Person", "Student") (uni-directional)
   *
   * Incompatible:
   *     !compatible("double", "int")   (in all directions)
   *
   * The concrete Typechecker has to decide on further issues, like
   *     !compatible("List<double>", "List<int>") 
   *     where e.g. Java and OCL/P differ in their answers
   *
   * @param left  Super-Type
   * @param right  Sub-Type (assignment-compatible to supertype?)
   *
   * TODO: Probably needs to be extended for free type-variable assignments
   * (because it may be that they get unified over time: e.g. Map<a,List<c>> and Map<long,b>
   * are compatible, by refining the assignments a-> long, b->List<c>
   *
   * TODO: remove "static" keyword
   */
  public static boolean compatible(SymTypeExpression left,
                                   SymTypeExpression right)
  {
    if(left.isObscureType() || right.isObscureType()){
      return true;
    }
    if(left.isPrimitive()&&right.isPrimitive()){
      SymTypePrimitive leftType = (SymTypePrimitive) left;
      SymTypePrimitive rightType = (SymTypePrimitive) right;
      if(isBoolean(leftType)&& isBoolean(rightType)){
        return true;
      }
      if(isDouble(leftType)&&rightType.isNumericType()){
        return true;
      }
      if(isFloat(leftType)&&((rightType.isIntegralType())|| isFloat(right))){
        return true;
      }
      if(isLong(leftType)&&rightType.isIntegralType()){
        return true;
      }
      if(isInt(leftType)&&rightType.isIntegralType()&&!isLong(right)){
        return true;
      }
      if(isChar(leftType)&& isChar(right)){
        return true;
      }
      if(isShort(leftType)&& isShort(right)){
        return true;
      }
      if(isByte(leftType)&& isByte(right)){
        return true;
      }
      return false;
    } else if (!left.isPrimitive() && right.isNullType()){
      return true;
    } else if(unbox(left.printFullName()).equals(unbox(right.printFullName()))) {
      return true;
    } else if(isSubtypeOf(right,left)) {
      return true;
    } else if (right.printFullName().equals(left.printFullName())) {
      return true;
    } else if (left.deepEquals(right) || right.deepEquals(left)) {
      return true;
    }
    return false;
  }


  /**
   * determines if one SymTypeExpression is a subtype of another SymTypeExpression
   * @param subType the SymTypeExpression that could be a subtype of the other SymTypeExpression
   * @param superType the SymTypeExpression that could be a supertype of the other SymTypeExpression
   */
  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    if(subType.isObscureType() || superType.isObscureType()){
      return true;
    }
    if(unbox(subType.printFullName()).equals(unbox(superType.printFullName()))){
      return true;
    }
    if(subType.isPrimitive()&&superType.isPrimitive()) {
      SymTypePrimitive sub = (SymTypePrimitive) subType;
      SymTypePrimitive supert = (SymTypePrimitive) superType;
      if (isDouble(supert) && sub.isNumericType() &&!isDouble(sub)) {
        return true;
      }
      if (isFloat(supert) && sub.isIntegralType()) {
        return true;
      }
      if (isLong(supert) && sub.isIntegralType() && !isLong(subType)) {
        return true;
      }
      if (isInt(supert) && sub.isIntegralType() && !isLong(subType) && !isInt(subType)) {
        return true;
      }
      return false;
    }else if((subType.isPrimitive() && !superType.isPrimitive()) ||
        (superType.isPrimitive() && !subType.isPrimitive())){
      return false;
    }
    return isSubtypeOfRec(subType,superType);
  }


  /**
   * private recursive helper method for the method isSubTypeOf
   * @param subType the SymTypeExpression that could be a subtype of the other SymTypeExpression
   * @param superType the SymTypeExpression that could be a supertype of the other SymTypeExpression
   */
  protected static boolean isSubtypeOfRec(SymTypeExpression subType, SymTypeExpression superType){
    if (!subType.getTypeInfo().getSuperTypesList().isEmpty()) {
      for (SymTypeExpression type : subType.getTypeInfo().getSuperTypesList()) {
        if(type.print().equals(superType.print())){
          return true;
        }
        if(type.isGenericType() && superType.isGenericType()){
          //TODO check recursively, this is only a hotfix, see #2977
          SymTypeOfGenerics typeGen = (SymTypeOfGenerics) type;
          SymTypeOfGenerics supTypeGen = (SymTypeOfGenerics) superType;
          if(typeGen.printTypeWithoutTypeArgument().equals(supTypeGen.printTypeWithoutTypeArgument())
          && typeGen.sizeArguments() == supTypeGen.sizeArguments()){
            boolean success = true;
            for(int i = 0; i<typeGen.sizeArguments(); i++){
              if(!typeGen.getArgument(i).isTypeVariable()){
                if(!typeGen.getArgument(i).print().equals(supTypeGen.getArgument(i).print())){
                  success = false;
                }
              }
            }
            if(success){
              return true;
            }
          }
        }
      }
    }
    boolean subtype = false;
    for (int i = 0; i < subType.getTypeInfo().getSuperTypesList().size(); i++) {
      if (isSubtypeOf(subType.getTypeInfo().getSuperTypesList().get(i), superType)) {
        subtype=true;
        break;
      }
    }
    return subtype;
  }

  public static boolean isBoolean(SymTypeExpression type) {
    return type.isObscureType() || "boolean".equals(unbox(type.print()));
  }

  public static boolean isInt(SymTypeExpression type) {
    return type.isObscureType() || "int".equals(unbox(type.print()));
  }

  public static boolean isDouble(SymTypeExpression type) {
    return type.isObscureType() || "double".equals(unbox(type.print()));
  }

  public static boolean isFloat(SymTypeExpression type) {
    return type.isObscureType() || "float".equals(unbox(type.print()));
  }

  public static boolean isLong(SymTypeExpression type) {
    return type.isObscureType() || "long".equals(unbox(type.print()));
  }

  public static boolean isChar(SymTypeExpression type) {
    return type.isObscureType() || "char".equals(unbox(type.print()));
  }

  public static boolean isShort(SymTypeExpression type) {
    return type.isObscureType() || "short".equals(unbox(type.print()));
  }

  public static boolean isByte(SymTypeExpression type) {
    return type.isObscureType() || "byte".equals(unbox(type.print()));
  }

  public static boolean isVoid(SymTypeExpression type) {
    return type.isObscureType() || "void".equals(unbox(type.print()));
  }

  public static boolean isString(SymTypeExpression type) {
    return type.isObscureType() || "String".equals(type.print());
  }
}

