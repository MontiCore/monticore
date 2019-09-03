/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.SymTypeOfObject;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;

import static de.monticore.typescalculator.TypesCalculatorHelper.isIntegralType;
import static de.monticore.typescalculator.TypesCalculatorHelper.unbox;

public class TypesCalculator {

  private static IExpressionAndLiteralsTypeCalculatorVisitor calc;

  private static IExpressionsBasisScope scope;

  public static boolean isBoolean(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("boolean");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isString(ASTExpression expr){
    SymTypeExpression exp = new SymTypeOfObject();
    exp.setName("String");
    if(exp.deepEquals(unbox(calc.calculateType(expr)))){
      return true;
    }
    return false;
  }

  public static boolean isInt(ASTExpression expr) {
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("int");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isLong(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("long");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isChar(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("char");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isFloat(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("float");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isDouble(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("double");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isShort(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("short");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isByte(ASTExpression expr){
    SymTypeExpression exp = new SymTypeConstant();
    exp.setName("byte");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isPrimitive(ASTExpression expr){
    return isBoolean(expr)||isInt(expr)||isLong(expr)||isChar(expr)||isDouble(expr)||isShort(expr)||isByte(expr)||isFloat(expr);
  }

  public static String getTypeString(ASTExpression expr){
    calc.calculateType(expr);
    String result = "";
    if(calc.getTypes().get(expr)!=null) {
      result = calc.getTypes().get(expr).getName();
    }
    return result;
  }

  public static SymTypeExpression getType(ASTExpression expr){
    return calc.calculateType(expr);
  }

  public static boolean isAssignableFrom(ASTExpression left, ASTExpression right){
    calc.calculateType(left);
    calc.calculateType(right);
    if(calc.getTypes().get(left)!=null){
      if(isPrimitive(left)&&isPrimitive(right)){
        if(isBoolean(left)&&isBoolean(right)){
          return true;
        }
        if(isDouble(left)&&TypesCalculatorHelper.isNumericType(calc.getTypes().get(right))){
          return true;
        }
        if(isFloat(left)&&(isIntegralType(calc.getTypes().get(right))||isFloat(right))){
          return true;
        }
        if(isLong(left)&&isIntegralType(calc.getTypes().get(right))){
          return true;
        }
        if(isInt(left)&&isIntegralType(calc.getTypes().get(right))&&!isLong(right)){
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
        if(isSubtypeOf(right,left)||calc.getTypes().get(right).deepEquals(calc.getTypes().get(left))){
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isSubtypeOf(ASTExpression subType, ASTExpression superType){
    calc.calculateType(subType);
    calc.calculateType(superType);

    if(isPrimitive(subType)&&isPrimitive(superType)) {
      if (isBoolean(superType) && isBoolean(subType)) {
        return true;
      }
      if (isDouble(superType) && TypesCalculatorHelper.isNumericType(calc.getTypes().get(subType))&&!isDouble(subType)) {
        return true;
      }
      if (isFloat(superType) && isIntegralType(calc.getTypes().get(subType))) {
        return true;
      }
      if (isLong(superType) && isIntegralType(calc.getTypes().get(subType)) && !isLong(subType)) {
        return true;
      }
      if (isInt(superType) && isIntegralType(calc.getTypes().get(subType)) && !isLong(subType) && !isInt(subType)) {
        return true;
      }
      return false;
    }
    return isSubtypeOf(calc.getTypes().get(subType),calc.getTypes().get(superType));
  }

  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    if(!subType.getSuperTypes().isEmpty()){
      for(SymTypeExpression type: subType.getSuperTypes()){
        if(type.deepEquals(superType)){
          return true;
        }
      }
    }
    boolean subtype = false;
    for(int i = 0;i<subType.getSuperTypes().size();i++){
      if(isSubtypeOf(subType.getSuperTypes().get(i),superType)){
        subtype=true;
        break;
      }
    }
    return subtype;
  }

  public static boolean isBoolean(SymTypeExpression type){
    return "boolean".equals(unbox(type).getName());
  }

  public static boolean isInt(SymTypeExpression type){
    return "int".equals(unbox(type).getName());
  }

  public static boolean isDouble(SymTypeExpression type){
    return "double".equals(unbox(type).getName());
  }

  public static boolean isFloat(SymTypeExpression type){
    return "float".equals(unbox(type).getName());
  }

  public static boolean isLong(SymTypeExpression type){
    return "long".equals(unbox(type).getName());
  }

  public static boolean isChar(SymTypeExpression type){
    return "char".equals(unbox(type).getName());
  }

  public static boolean isShort(SymTypeExpression type){
    return "short".equals(unbox(type).getName());
  }

  public static boolean isByte(SymTypeExpression type){
    return "byte".equals(unbox(type).getName());
  }

  public static boolean isVoid(SymTypeExpression type){
    return "void".equals(unbox(type).getName());
  }

  public static void setScope(IExpressionsBasisScope ascope){
    scope=ascope;
    calc.setScope(ascope);
  }

  public static void setExpressionAndLiteralsTypeCalculator(IExpressionAndLiteralsTypeCalculatorVisitor calc) {
    TypesCalculator.calc = calc;
  }

}
