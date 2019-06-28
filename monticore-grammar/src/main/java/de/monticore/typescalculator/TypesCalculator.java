package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.typescalculator.TypesCalculatorHelper.isIntegralType;
import static de.monticore.typescalculator.TypesCalculatorHelper.unbox;

public class TypesCalculator {

  private static IExpressionAndLiteralsTypeCalculatorVisitor calc;

  public static boolean isBoolean(ASTExpression expr){
    return calc.calculateType(expr).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
  }

  public static boolean isString(ASTExpression expr){
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    if(calc.calculateType(expr).deepEquals(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())){
      return true;
    }
    name.remove("java");
    name.remove("lang");
    if(calc.calculateType(expr).deepEquals(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build())){
      return true;
    }
    return false;
  }

  public static boolean isInt(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build());
  }

  public static boolean isLong(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build());
  }

  public static boolean isChar(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build());
  }

  public static boolean isFloat(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build());
  }

  public static boolean isDouble(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build());
  }

  public static boolean isShort(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.SHORT).build());
  }

  public static boolean isByte(ASTExpression expr){
    return unbox(calc.calculateType(expr)).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build());
  }

  public static boolean isPrimitive(ASTExpression expr){
    return isBoolean(expr)||isInt(expr)||isLong(expr)||isChar(expr)||isDouble(expr)||isShort(expr)||isByte(expr)||isFloat(expr);
  }

  public static String getTypeString(ASTExpression expr){
    calc.calculateType(expr);
    String result = "";
    if(calc.getTypes().get(expr)!=null) {
      for (String part : calc.getTypes().get(expr).getASTMCType().getNameList()) {
        result += part + ".";
      }
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public static ASTMCType getType(ASTExpression expr){
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
        if(isDouble(left)&&TypesCalculatorHelper.isNumericType(calc.getTypes().get(right).getASTMCType())){
          return true;
        }
        if(isFloat(left)&&(isIntegralType(calc.getTypes().get(right).getASTMCType())||isFloat(right))){
          return true;
        }
        if(isLong(left)&&isIntegralType(calc.getTypes().get(right).getASTMCType())){
          return true;
        }
        if(isInt(left)&&isIntegralType(calc.getTypes().get(right).getASTMCType())&&!isLong(right)){
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
        if(isSubtypeOf(right,left)||calc.getTypes().get(right).getASTMCType().deepEquals(calc.getTypes().get(left).getASTMCType())){
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isSubtypeOf(ASTExpression subType, ASTExpression superType){
    calc.calculateType(subType);
    calc.calculateType(superType);
    return isSubtypeOf(calc.getTypes().get(subType),calc.getTypes().get(superType));
  }

  private static boolean isSubtypeOf(MCTypeSymbol subType, MCTypeSymbol superType){
    if(!subType.getSupertypes().isEmpty()&&subType.getSupertypes().contains(superType)){
      return true;
    }
    boolean subtype = false;
    for(int i = 0;i<subType.getSupertypes().size();i++){
      if(isSubtypeOf(subType.getSupertypes().get(i),superType)){
        subtype=true;
        break;
      }
    }
    return subtype;
  }

  public static boolean isBoolean(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
  }

  public static boolean isInt(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build());
  }

  public static boolean isDouble(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build());
  }

  public static boolean isFloat(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build());
  }

  public static boolean isLong(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build());
  }

  public static boolean isChar(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build());
  }

  public static boolean isShort(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.SHORT).build());
  }

  public static boolean isByte(ASTMCType type){
    return unbox(type).deepEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build());
  }

  public static void setScope(ExpressionsBasisScope scope){
    calc.setScope(scope);
  }

  public static void setExpressionAndLiteralsTypeCalculator(IExpressionAndLiteralsTypeCalculatorVisitor calc) {
    TypesCalculator.calc = calc;
  }

}
