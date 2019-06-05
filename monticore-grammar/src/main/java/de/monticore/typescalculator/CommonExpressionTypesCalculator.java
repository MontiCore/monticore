package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.monticore.typescalculator.TypesCalculatorHelper.*;

public class CommonExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements CommonExpressionsVisitor {

  private CommonExpressionsVisitor realThis;

  @Override
  public void setRealThis(CommonExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public CommonExpressionsVisitor getRealThis(){
    return realThis;
  }

  public CommonExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void endVisit(ASTPlusExpression expr){
    ASTMCType result = getBinaryNumericPromotionWithString(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0188 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMultExpression expr){
    ASTMCType result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0189 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    ASTMCType result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0190 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    ASTMCType result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0191 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTModuloExpression expr){
    ASTMCType result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0192 The resulting type cannot be calculated");
    }
  }
  
  @Override
  public void endVisit(ASTLessEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0193 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0194 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0195 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0196 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0197 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0198 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0199 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0200 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if (types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0201 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBracketExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      result=types.get(expr.getExpression()).getASTMCType().deepClone();
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0202 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getTrueExpression())&&types.containsKey(expr.getFalseExpression())){
      if(types.containsKey(expr.getCondition())&&types.get(expr.getCondition()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())){
        if(types.get(expr.getTrueExpression()).getASTMCType().deepEquals(types.get(expr.getFalseExpression()).getASTMCType())){
          result=types.get(expr.getFalseExpression()).getASTMCType().deepClone();
        }else{
          result=getBinaryNumericPromotion(expr.getTrueExpression(),expr.getFalseExpression());
        }
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error("0xA0204 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(isIntegralType(types.get(expr.getExpression()).getASTMCType())){
        result = getUnaryNumericPromotionType(types.get(expr.getExpression()).getASTMCType());
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error("0xA0205 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTCallExpression expr){
    if(types.containsKey(expr.getExpression())) {
      CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
      String exprString = printer.prettyprint(expr);
      ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      String exp = prettyPrinter.prettyprint(expr.getExpression());
      Collection<EMethodSymbol> methodcollection = scope.resolveEMethodDownMany(exp);
      List<EMethodSymbol> methodlist = new ArrayList<>(methodcollection);
      for (EMethodSymbol method : methodlist) {
        if (expr.getArguments().getExpressionList().size()==method.getArguments().size()){
          boolean success = true;
          for(int i=0;i<method.getArguments().size();i++){
            if(!method.getArguments().get(i).getASTMCType().deepEquals(types.get(expr.getArguments().getExpressionList().get(i)))){
              success = false;
            }
          }
          if(success){
            String nameString = printer.prettyprint(expr.getExpression());
            if(method.getReturnType().isPresentMCType()){
              ASTMCType result=method.getReturnType().getMCType();
              this.result=result;
              MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
              sym.setASTMCType(result);
              types.put(expr,sym);
            }else if(method.getReturnType().isPresentMCVoidType()){
              List<String> name = new ArrayList<>();
              name.add("void");
              ASTMCType result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
              this.result=result;
              MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
              sym.setASTMCType(result);
              types.put(expr,sym);
            }else{
              Log.error("0xA209 the resulting type cannot be resolved");
            }
          }else{
            Log.error("0xA209 the resulting type cannot be resolved");
          }
        }
      }
    }else{
      Log.info("package suspected","CommonExpressionTypesCalculator");
    }
  }

  private ASTMCType calculateTypeCompare(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isNumericType(types.get(left).getASTMCType())&&isNumericType(types.get(right).getASTMCType())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeLogical(ASTExpression left, ASTExpression right) {
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isPrimitiveType(types.get(left).getASTMCType())&&isPrimitiveType(types.get(right).getASTMCType())){
        return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
      if(!isPrimitiveType(types.get(left).getASTMCType())&&!isPrimitiveType(types.get(right).getASTMCType())&&(types.get(left).getASTMCType().deepEquals(types.get(right).getASTMCType())||types.get(left).getSubtypes().contains(types.get(right))||types.get(left).getSupertypes().contains(types.get(right)))){
        return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  public ASTMCType getBinaryNumericPromotion(ASTExpression leftType,
                                                    ASTExpression rightType) {
    if(types.containsKey(leftType)&&types.containsKey(rightType)){
      if("double".equals(unbox(types.get(leftType).getASTMCType()).getBaseName())||"double".equals(unbox(types.get(rightType).getASTMCType()).getBaseName())){
        return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
      if("float".equals(unbox(types.get(leftType).getASTMCType()).getBaseName())||"float".equals(unbox(types.get(rightType).getASTMCType()).getBaseName())){
        return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
      }
      if("long".equals(unbox(types.get(leftType).getASTMCType()).getBaseName())||"long".equals(unbox(types.get(rightType).getASTMCType()).getBaseName())){
        return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
      }
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
    }
    return null;
  }

  public ASTMCType getBinaryNumericPromotionWithString(ASTExpression leftType,
                                             ASTExpression rightType) {
    if(types.containsKey(leftType)&&types.containsKey(rightType)){
      List<String> name = new ArrayList<>();
      name.add("java");
      name.add("lang");
      name.add("String");
      if(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(types.get(leftType).getASTMCType())||MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(types.get(rightType).getASTMCType())){
        return MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
      }
      name.remove("java");
      name.remove("lang");
      if(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(types.get(leftType).getASTMCType())||MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(types.get(rightType).getASTMCType())){
        return MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
      }
      return getBinaryNumericPromotion(leftType,rightType);
    }
    return null;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }

  public void setTypes(Map<ASTNode,MCTypeSymbol> types){
    this.types=types;
  }
}
