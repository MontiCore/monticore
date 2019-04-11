package de.monticore.typescalculator;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.HashMap;
import java.util.Map;

public class CommonExpressionTypesCalculator implements CommonExpressionsVisitor {

  private Map<ASTExpression, ASTMCType> types;

  private ASTMCType result;

  public CommonExpressionTypesCalculator(){
    types = new HashMap<>();
  }

  @Override
  public void visit(ASTPlusExpression expr){

  }

  @Override
  public void endVisit(ASTPlusExpression expr){
    ASTMCType result=calculateType(expr);
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new NullPointerException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void visit(ASTMultExpression expr){

  }

  @Override
  public void endVisit(ASTMultExpression expr){
    ASTMCType result = calculateType(expr);
    if(result!=null){
      types.put(expr, result);
      this.result=result;
    }else{
      throw new NullPointerException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void visit(ASTDivideExpression expr){

  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    ASTMCType result = calculateType(expr);
    if(result!=null){
      types.put(expr, result);
      this.result=result;
    }else{
      throw new NullPointerException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void visit(ASTMinusExpression expr){

  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    ASTMCType result = calculateType(expr);
    if(result!=null){
      types.put(expr, result);
      this.result=result;
    }else{
      throw new NullPointerException("The resulting type cannot be calculated");
    }
  }

  public ASTMCType getResult() {
    return result;
  }

  public Map<ASTExpression, ASTMCType> getTypes() {
    return types;
  }

  public void setTypes(Map<ASTExpression, ASTMCType> types){
    this.types = types;
  }

  public void setResult(ASTMCType result) {
    this.result = result;
  }

  private ASTMCType calculateType(ASTBiOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(6)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(6))) {
        result = new ASTMCPrimitiveType(6);
      }else if(types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(4)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(4))){
        result = new ASTMCPrimitiveType(4);
      }else if(types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(5)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(5))){
        result = new ASTMCPrimitiveType(5);
      }else if(types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(7)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(7))){
        result = new ASTMCPrimitiveType(7);
      }else if(types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(6)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(4)) || types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(4)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(6))){
        result = new ASTMCPrimitiveType(4);
      }
    }

    return result;
  }
}
