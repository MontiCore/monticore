package de.monticore.typescalculator;

import de.monticore.common.common._ast.ASTConstantsCommon;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsInheritanceVisitor;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
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
  public void endVisit(ASTPlusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMultExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }
  
  @Override
  public void endVisit(ASTLessEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if (types.get(expr.getExpression()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBracketExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      result=types.get(expr.getExpression());
    }
    if(result!=null) {
      types.put(expr, result);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTSimpleAssignmentExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())){
      if(types.get(expr.getLeft()).deepEquals(types.get(expr.getRight()))){
        result=types.get(expr.getRight());
        this.result=result;
        return;
      }else if(types.get(expr.getLeft()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))&&types.get(expr.getRight()).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
        this.result=result;
      }
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

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))){
        result = new ASTMCPrimitiveType(5);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))){
        result = new ASTMCPrimitiveType(7);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))){
        result = new ASTMCPrimitiveType(4);
      }
    }
    return result;
  }

  private ASTMCType calculateTypeCompare(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    return result;
  }

  private ASTMCType calculateTypeLogical(ASTExpression left, ASTExpression right) {
    ASTMCType result = null;
    if (types.containsKey(left) && types.containsKey(right)) {
      if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(right).deepEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    return result;
  }
}
