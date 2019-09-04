/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types2.SymTypeOfObject;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.monticore.typescalculator.TypesCalculator.isSubtypeOf;
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
    SymTypeExpression result = getBinaryNumericPromotionWithString(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0188 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMultExpression expr){
    SymTypeExpression result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0189 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    SymTypeExpression result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0190 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    SymTypeExpression result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0191 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTModuloExpression expr){
    SymTypeExpression result = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0192 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessEqualExpression expr){
    SymTypeExpression result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0193 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr){
    SymTypeExpression result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0194 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression expr){
    SymTypeExpression result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0195 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr){
    SymTypeExpression result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0196 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression expr){
    SymTypeExpression result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0197 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr){
    SymTypeExpression result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0198 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    SymTypeExpression result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      SymTypeExpression exp = new SymTypeConstant();
      exp.setName("boolean");
      if (types.get(expr.getLeft()).deepEquals(exp) && types.get(expr.getRight()).deepEquals(exp)) {
        result = new SymTypeConstant();
        result.setName("boolean");
      }
    }
    if(result!=null) {
      SymTypeExpression sym = new SymTypeConstant();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0199 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    SymTypeExpression result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      SymTypeExpression exp = new SymTypeConstant();
      exp.setName("boolean");
      if (types.get(expr.getLeft()).deepEquals(exp) && types.get(expr.getRight()).deepEquals(exp)) {
        result = new SymTypeConstant();
        result.setName("boolean");
      }
    }
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0200 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    SymTypeExpression result = null;
    if(types.containsKey(expr.getExpression())){
      SymTypeExpression exp = new SymTypeConstant();
      exp.setName("boolean");
      if (types.get(expr.getExpression()).deepEquals(exp)) {
        result = new SymTypeConstant();
        result.setName("boolean");
      }
    }
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0201 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBracketExpression expr){
    SymTypeExpression result = null;
    if(types.containsKey(expr.getExpression())){
      result=types.get(expr.getExpression()).deepClone();
    }
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0202 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression expr){
    SymTypeExpression result = null;
    if(types.containsKey(expr.getTrueExpression())&&types.containsKey(expr.getFalseExpression())){
      SymTypeExpression exp = new SymTypeConstant();
      exp.setName("boolean");
      if(types.containsKey(expr.getCondition())&&types.get(expr.getCondition()).deepEquals(exp)){
        if(types.get(expr.getTrueExpression()).deepEquals(types.get(expr.getFalseExpression()))){
          result=types.get(expr.getFalseExpression()).deepClone();
        }else{
          result=getBinaryNumericPromotion(expr.getTrueExpression(),expr.getFalseExpression());
        }
      }
    }
    if(result!=null) {
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0204 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr){
    SymTypeExpression result = null;
    if(types.containsKey(expr.getExpression())){
      if(isIntegralType(types.get(expr.getExpression()))){
        result = getUnaryNumericPromotionType(types.get(expr.getExpression()));
      }
    }
    if(result!=null){
      SymTypeExpression sym = result.deepClone();
      sym.setName(result.getName());
      types.put(expr, sym);
      this.result = sym;
    }else{
      Log.error("0xA0205 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTCallExpression expr){
    if(types.containsKey(expr.getExpression())) {
      CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
      ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      String exp = prettyPrinter.prettyprint(expr.getExpression());
      Collection<EMethodSymbol> methodcollection = scope.resolveEMethodMany(exp);
      List<EMethodSymbol> methodlist = new ArrayList<>(methodcollection);
      for (EMethodSymbol method : methodlist) {
        if (expr.getArguments().getExpressionList().size()==method.getParameterList().size()){
          boolean success = true;
          for(int i=0;i<method.getParameterList().size();i++){
            if(!method.getParameterList().get(i).getType().deepEquals(types.get(expr.getArguments().getExpressionList().get(i)))&&!isSubtypeOf(types.get(expr.getArguments().getExpressionList().get(i)),method.getParameterList().get(i).getType())){
              success = false;
            }
          }
          if(success){
            if(!"void".equals(method.getReturnType().getName())){
              SymTypeExpression result=method.getReturnType();
              this.result=result;
              types.put(expr,result);
            }else if("void".equals(method.getReturnType().getName())){
              SymTypeExpression result = new SymTypeConstant();
              result.setName("void");
              types.put(expr, result);
              this.result = result;
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

  private SymTypeExpression calculateTypeCompare(ASTExpression left, ASTExpression right){
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isNumericType(types.get(left))&&isNumericType(types.get(right))){
        result = new SymTypeConstant();
        result.setName("boolean");
      }
    }
    return result;
  }

  private SymTypeExpression calculateTypeLogical(ASTExpression left, ASTExpression right) {
    SymTypeExpression result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if(isPrimitiveType(types.get(left))&&isPrimitiveType(types.get(right))){
        result = new SymTypeConstant();
        result.setName("boolean");
        return result;
      }
      if(!isPrimitiveType(types.get(left)) && !isPrimitiveType(types.get(right)) &&
              (          types.get(left).deepEquals(types.get(right))
                      || types.get(right).getSuperTypes().contains(types.get(left))
                      || types.get(left).getSuperTypes().contains(types.get(right))
              )){
        result = new SymTypeConstant();
        result.setName("boolean");
        return result;
      }
    }
    return result;
  }

  public SymTypeExpression getBinaryNumericPromotion(ASTExpression leftType,
                                                     ASTExpression rightType) {
    SymTypeExpression result = null;
    if(types.containsKey(leftType)&&types.containsKey(rightType)){
      if("double".equals(unbox(types.get(leftType)).getName())||"double".equals(unbox(types.get(rightType)).getName())){
        result = new SymTypeConstant();
        result.setName("double");
        return result;
      }
      if("float".equals(unbox(types.get(leftType)).getName())||"float".equals(unbox(types.get(rightType)).getName())){
        result = new SymTypeConstant();
        result.setName("float");
        return result;
      }
      if("long".equals(unbox(types.get(leftType)).getName())||"long".equals(unbox(types.get(rightType)).getName())){
        result = new SymTypeConstant();
        result.setName("long");
        return result;
      }
      result = new SymTypeConstant();
      result.setName("int");
    }
    return result;
  }

  public SymTypeExpression getBinaryNumericPromotionWithString(ASTExpression leftType,
                                                               ASTExpression rightType) {
    SymTypeExpression result = null;
    if(types.containsKey(leftType)&&types.containsKey(rightType)){
      SymTypeExpression exp = new SymTypeOfObject();
      exp.setName("java.lang.String");
      if(exp.deepEquals(types.get(leftType))||exp.deepEquals(types.get(rightType))){
        result = new SymTypeOfObject();
        result.setName("String");
        return result;
      }
      exp.setName("String");
      if(exp.deepEquals(types.get(leftType))||exp.deepEquals(types.get(rightType))){
        result = new SymTypeOfObject();
        result.setName("String");
        return result;
      }
      return getBinaryNumericPromotion(leftType,rightType);
    }
    return result;
  }

  public SymTypeExpression calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr);
  }

  public void setTypes(Map<ASTNode, SymTypeExpression> types){
    this.types=types;
  }
}
