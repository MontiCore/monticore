/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;

import static de.monticore.typescalculator.TypesCalculatorHelper.isIntegralType;
import static de.monticore.typescalculator.TypesCalculatorHelper.unbox;

public class TypesCalculator {

  private static IExpressionAndLiteralsTypeCalculatorVisitor calc;

  private static IExpressionsBasisScope scope;

  public static boolean isBoolean(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("boolean");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isString(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("String");
    if(exp.deepEquals(unbox(calc.calculateType(expr)))){
      return true;
    }
    return false;
  }

  public static boolean isInt(ASTExpression expr) {
    TypeExpression exp = new TypeExpression();
    exp.setName("int");
    return exp.deepEquals(unbox(calc.calculateType(expr)));
  }

  public static boolean isLong(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("long");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

  public static boolean isChar(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("char");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

  public static boolean isFloat(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("float");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

  public static boolean isDouble(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("double");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

  public static boolean isShort(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("short");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

  public static boolean isByte(ASTExpression expr){
    TypeExpression exp = new TypeExpression();
    exp.setName("byte");
    return exp.deepEquals(unbox(calc.calculateType(expr)));  }

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

  public static TypeExpression getType(ASTExpression expr){
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

  public static boolean isSubtypeOf(TypeExpression subType, TypeExpression superType){
    if(!subType.getSuperTypes().isEmpty()){
      for(TypeExpression type: subType.getSuperTypes()){
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

  public static boolean isBoolean(TypeExpression type){
    return unbox(type).getName().equals("boolean");
  }

  public static boolean isInt(TypeExpression type){
    return unbox(type).getName().equals("int");
  }

  public static boolean isDouble(TypeExpression type){
    return unbox(type).getName().equals("double");
  }

  public static boolean isFloat(TypeExpression type){
    return unbox(type).getName().equals("float");
  }

  public static boolean isLong(TypeExpression type){
    return unbox(type).getName().equals("long");
  }

  public static boolean isChar(TypeExpression type){
    return unbox(type).getName().equals("char");
  }

  public static boolean isShort(TypeExpression type){
    return unbox(type).getName().equals("short");
  }

  public static boolean isByte(TypeExpression type){
    return unbox(type).getName().equals("byte");
  }

  public static boolean isVoid(TypeExpression type){
    return type.getName().equals("void");
  }

  public static void setScope(IExpressionsBasisScope ascope){
    scope=ascope;
    calc.setScope(ascope);
  }

  public static void setExpressionAndLiteralsTypeCalculator(IExpressionAndLiteralsTypeCalculatorVisitor calc) {
    TypesCalculator.calc = calc;
  }

}
