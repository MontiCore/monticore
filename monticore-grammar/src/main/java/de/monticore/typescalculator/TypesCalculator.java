/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.ITypesCalculator;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;

import java.util.Optional;

import static de.monticore.types2.SymTypeConstant.unbox;
import static de.monticore.typescalculator.TypesCalculatorHelper.isIntegralType;

public class TypesCalculator {

  private static ITypesCalculator calc;

  private static IExpressionsBasisScope scope;

  public static boolean isBoolean(ASTExpression expr){
    return "boolean".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isString(ASTExpression expr){
    return "String".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isInt(ASTExpression expr) {
    return "int".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isLong(ASTExpression expr){
    return "long".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isChar(ASTExpression expr){
    return "char".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isFloat(ASTExpression expr){
    return "float".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isDouble(ASTExpression expr){
    return "double".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isShort(ASTExpression expr){
    return "short".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isByte(ASTExpression expr){
    return "byte".equals(unbox(calc.calculateType(expr).get().print()));
  }

  public static boolean isPrimitive(ASTExpression expr){
    return isBoolean(expr)||isInt(expr)||isLong(expr)||isChar(expr)||isDouble(expr)||isShort(expr)||isByte(expr)||isFloat(expr);
  }

  public static String getTypeString(ASTExpression expr){
    return calc.calculateType(expr).get().print();
  }

  public static SymTypeExpression getType(ASTExpression expr){
    return calc.calculateType(expr).get();
  }

  public static boolean isAssignableFrom(ASTExpression left, ASTExpression right){
    Optional<SymTypeExpression> leftType = calc.calculateType(left);
    Optional<SymTypeExpression> rightType = calc.calculateType(right);
    if(isPrimitive(left)&&isPrimitive(right)){
      if(isBoolean(left)&&isBoolean(right)){
        return true;
      }
      if(isDouble(left)&&TypesCalculatorHelper.isNumericType(rightType.get())){
        return true;
      }
      if(isFloat(left)&&(isIntegralType(rightType.get())||isFloat(right))){
        return true;
      }
      if(isLong(left)&&isIntegralType(rightType.get())){
        return true;
      }
      if(isInt(left)&&isIntegralType(rightType.get())&&!isLong(right)){
        return true;
      }
      if(isChar(left)&&isChar(right)){
        return true;
      }
      if(isShort(left)&&isShort(right)){
        return true;
      }
      if(isByte(left)&&isByte(right)){
        return true;
      }
      return false;
    }else {
      if(isSubtypeOf(right,left)||rightType.get().print().equals(leftType.get().print())){
        return true;
      }
    }
    return false;
  }

  public static boolean isAssignableFrom(SymTypeExpression left, SymTypeExpression right){
    if(left.isPrimitiveType()&&right.isPrimitiveType()){
      SymTypeConstant leftType = (SymTypeConstant) left;
      SymTypeConstant rightType = (SymTypeConstant) right;
      if(isBoolean(leftType)&&isBoolean(rightType)){
        return true;
      }
      if(isDouble(leftType)&&rightType.isNumericType()){
        return true;
      }
      if(isFloat(leftType)&&(rightType.isIntegralType())||isFloat(right)){
        return true;
      }
      if(isLong(leftType)&&rightType.isIntegralType()){
        return true;
      }
      if(isInt(leftType)&&rightType.isIntegralType()&&!isLong(right)){
        return true;
      }
      if(isChar(leftType)&&isChar(right)){
        return true;
      }
      if(isShort(leftType)&&isShort(right)){
        return true;
      }
      if(isByte(leftType)&&isByte(right)){
        return true;
      }
      return false;
    }else {
      if(isSubtypeOf(right,left)||right.print().equals(left.print())){
        return true;
      }
    }
    return false;
  }

  public static boolean isSubtypeOf(ASTExpression subType, ASTExpression superType){
    Optional<SymTypeExpression> sub = calc.calculateType(subType);
    Optional<SymTypeExpression> supert = calc.calculateType(superType);

    if(isPrimitive(subType)&&isPrimitive(superType)) {
      if (isBoolean(superType) && isBoolean(subType)) {
        return true;
      }
      if (isDouble(superType) && TypesCalculatorHelper.isNumericType(sub.get())&&!isDouble(subType)) {
        return true;
      }
      if (isFloat(superType) && isIntegralType(sub.get())) {
        return true;
      }
      if (isLong(superType) && isIntegralType(sub.get()) && !isLong(subType)) {
        return true;
      }
      if (isInt(superType) && isIntegralType(sub.get()) && !isLong(subType) && !isInt(subType)) {
        return true;
      }
      return false;
    }
    return isSubtypeOf(sub.get(),supert.get());
  }

  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    if(!subType.getTypeInfo().getSuperTypeList().isEmpty()){
      for(SymTypeExpression type: subType.getTypeInfo().getSuperTypeList()){
        if(type.print().equals(superType.print())){
          return true;
        }
      }
    }
    boolean subtype = false;
    for(int i = 0;i<subType.getTypeInfo().getSuperTypeList().size();i++){
      if(isSubtypeOf(subType.getTypeInfo().getSuperTypeList().get(i),superType)){
        subtype=true;
        break;
      }
    }
    return subtype;
  }

  public static boolean isBoolean(SymTypeExpression type){
    return "boolean".equals(unbox(type.print()));
  }

  public static boolean isInt(SymTypeExpression type){
    return "int".equals(unbox(type.print()));
  }

  public static boolean isDouble(SymTypeExpression type){
    return "double".equals(unbox(type.print()));
  }

  public static boolean isFloat(SymTypeExpression type){
    return "float".equals(unbox(type.print()));
  }

  public static boolean isLong(SymTypeExpression type){
    return "long".equals(unbox(type.print()));
  }

  public static boolean isChar(SymTypeExpression type){
    return "char".equals(unbox(type.print()));
  }

  public static boolean isShort(SymTypeExpression type){
    return "short".equals(unbox(type.print()));
  }

  public static boolean isByte(SymTypeExpression type){
    return "byte".equals(unbox(type.print()));
  }

  public static boolean isVoid(SymTypeExpression type){
    return "void".equals(unbox(type.print()));
  }

//  public static void setScope(IExpressionsBasisScope ascope){
//    scope=ascope;
//    calc.setScope(ascope);
//  }

  public static void setExpressionAndLiteralsTypeCalculator(ITypesCalculator calc) {
    TypesCalculator.calc = calc;
  }

}
