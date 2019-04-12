package de.monticore.typescalculator;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsInheritanceVisitor;
import de.monticore.expressions.commonexpressions._ast.ASTDivideExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.HashMap;
import java.util.Map;

public class AssignmentExpressionTypesCalculator implements AssignmentExpressionsInheritanceVisitor {

  private Map<ASTExpression, ASTMCType> types;

  private ASTMCType result;

  public AssignmentExpressionTypesCalculator(){
    types = new HashMap<>();
  }

  @Override
  public void endVisit(ASTIncSuffixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTDecSuffixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTIncPrefixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTDecPrefixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTPlusPrefixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.INT)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }

    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTPlusAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTMinusAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTMultAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTDivideAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  @Override
  public void endVisit(ASTRegularAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTAndAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTOrAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTBinaryXorAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTRightShiftAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTLeftShiftAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTLogicalRightAssignmentExpression expr){

  }

  @Override
  public void endVisit(ASTModuloAssignmentExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null){
      this.result = result;
      types.put(expr,result);
    }else{
      throw new RuntimeException("the resulting type cannot be resolved");
    }
  }

  public Map<ASTExpression, ASTMCType> getTypes() {
    return types;
  }

  public ASTMCType getResult() {
    return result;
  }

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEquals(ASTConstantsMCBasicTypes.INT) && types.get(right).deepEquals(ASTConstantsMCBasicTypes.INT)) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }
      else if (types.get(left).deepEquals(ASTConstantsMCBasicTypes.DOUBLE) && types.get(right).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
      else if (types.get(left).deepEquals(ASTConstantsMCBasicTypes.DOUBLE) && types.get(right).deepEquals(ASTConstantsMCBasicTypes.INT) || types.get(left).deepEquals(ASTConstantsMCBasicTypes.INT) && types.get(right).deepEquals(ASTConstantsMCBasicTypes.DOUBLE)) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }
    return result;
  }

  //TODO: bisher nur double und int behandelt, es fehlen noch RegularAssignmentExpr, AndAssignmentExpr, OrAssignmentExpr, BinaryXorAssignmentExpr, RightShiftAssignmentExpr, LeftShiftAssignmentExpr, LogicalRightAssignmentExpr und die Tests
}
