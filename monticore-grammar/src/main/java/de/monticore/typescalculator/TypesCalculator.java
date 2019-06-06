package de.monticore.typescalculator;

import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.typescalculator.TypesCalculatorHelper.isIntegralType;
import static de.monticore.typescalculator.TypesCalculatorHelper.unbox;

public class TypesCalculator {

  private static CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(null); // was muss hier hin?

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

  public static boolean isString_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isString(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isBoolean_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isBoolean(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isInt_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isInt(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isDouble_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isDouble(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isFloat_StringExpression(String a)throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExp = p.parse_StringExpression(a);
    if(optExp.isPresent()){
      ASTExpression expr = optExp.get();
      return isFloat(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isLong_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isLong(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isChar_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isChar(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isShort_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isShort(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isByte_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isByte(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static boolean isPrimitive_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return isBoolean(expr)||isInt(expr)||isLong(expr)||isChar(expr)||isDouble(expr)||isShort(expr)||isByte(expr)||isFloat(expr);
    }else{
      Log.error("given expression not correct");
    }
    return false;
  }

  public static String getTypeString_StringExpression(String a)throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return getTypeString(expr);
    }else{
      Log.error("given expression not correct");
    }
    return null;
  }

  public static ASTMCType getType_StringExpression(String a) throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpr = p.parse_StringExpression(a);
    if(optExpr.isPresent()){
      ASTExpression expr = optExpr.get();
      return getType(expr);
    }else{
      Log.error("given expression not correct");
    }
    return null;
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
        return calc.getTypes().get(left).getSubtypes().contains(calc.getTypes().get(right));
      }
    }
    return false;
  }

  public static boolean isSubtypeOf(ASTExpression subType, ASTExpression superType){
    calc.calculateType(subType);
    calc.calculateType(superType);
    if(calc.getTypes().get(superType).getSubtypes().contains(calc.getTypes().get(subType))) {
      return true;
    }
    return false;
  }

  public static boolean isSubtypeOf_StringExpression(String subType, String superType)throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpA = p.parse_StringExpression(subType);
    Optional<ASTExpression> optExpB = p.parse_StringExpression(superType);
    if(optExpA.isPresent()&&optExpB.isPresent()){
      ASTExpression exprA = optExpA.get();
      ASTExpression exprB = optExpB.get();
      return isSubtypeOf(exprA,exprB);
    }
    Log.error("given expression not correct.");
    return false;
  }

  public static boolean isAssignableFrom_StringExpression(String left, String right) throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optExpA = p.parse_StringExpression(left);
    Optional<ASTExpression> optExpB = p.parse_StringExpression(right);
    if(optExpA.isPresent()&&optExpB.isPresent()){
      ASTExpression exprA = optExpA.get();
      ASTExpression exprB = optExpB.get();
      return isAssignableFrom(exprA,exprB);
    }
    Log.error("given expression not correct");
    return false;
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

}
