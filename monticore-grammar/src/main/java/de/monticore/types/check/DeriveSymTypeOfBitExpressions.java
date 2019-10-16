/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;

/**
 *  Visitor for BitExpressions
 */
public class DeriveSymTypeOfBitExpressions extends DeriveSymTypeOfExpression implements BitExpressionsVisitor {

  private BitExpressionsVisitor realThis;

  @Override
  public void setRealThis(BitExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public BitExpressionsVisitor getRealThis(){
    return realThis;
  }

  public DeriveSymTypeOfBitExpressions(){
    realThis = this;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression expr) {
    expr.accept(getRealThis());
    Optional<SymTypeExpression> result = lastResult.getLastOpt();
    lastResult.setLastOpt(Optional.empty());
    return result;
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }

  @Override
  public void traverse(ASTLeftShiftExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeShift(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0209 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTRightShiftExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeShift(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0210 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeShift(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0211 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBinaryAndExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0212 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0213 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBinaryXorExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the left expression in a variable for later use
      leftResult = lastResult.getLast();
    }else{
      Log.error("The type of the left expression could not be calculated");
    }

    expr.getRight().accept(getRealThis());
    if(lastResult.isPresentLast()){
      //store the type of the right expression in a variable for later use
      rightResult = lastResult.getLast();
    }else{
      Log.error("The type of the right expression could not be calculated");
    }

    Optional<SymTypeExpression> wholeResult = calculateTypeBinary(leftResult,rightResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0214 The resulting type cannot be calculated");
    }
  }

  /**
   * helper method for the calculation of the type of the ShiftExpressions
   */
  private Optional<SymTypeExpression> calculateTypeShift(SymTypeExpression left, SymTypeExpression right){
    if(left.isPrimitive()&&right.isPrimitive()){
      SymTypeConstant leftEx = (SymTypeConstant) left;
      SymTypeConstant rightEx = (SymTypeConstant) right;

      //only defined on integral type - integral type
      if(leftEx.isIntegralType()&&rightEx.isIntegralType()){
        return shiftCalculator(left,right);
      }
    }
    //should not happen, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the BinaryExpressions
   */
  private Optional<SymTypeExpression> calculateTypeBinary(SymTypeExpression left, SymTypeExpression right){
    if(left.isPrimitive()&&right.isPrimitive()) {
      SymTypeConstant leftEx = (SymTypeConstant) left;
      SymTypeConstant rightEx = (SymTypeConstant) right;

      //only defined on boolean - boolean and integral type - integral type
      if ("boolean".equals(unbox(left.print())) && "boolean".equals(unbox(right.print()))) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
      }else if (leftEx.isIntegralType()&&rightEx.isIntegralType()) {
        return getBinaryNumericPromotion(left,right);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  private Optional<SymTypeExpression> shiftCalculator(SymTypeExpression left, SymTypeExpression right){
    if(left.isPrimitive() && right.isPrimitive()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;

      //only defined on integral type - integral type
      if(leftResult.isIntegralType()&&rightResult.isIntegralType()){
        if(unbox(rightResult.print()).equals("long")){
          if(unbox(leftResult.print()).equals("long")){
            return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
          }else{
            return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
          }
        }else{
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the BinaryExpressions
   */
  private Optional<SymTypeExpression> getBinaryNumericPromotion(SymTypeExpression left, SymTypeExpression right){
    //only integral type - integral type
    if(left.isPrimitive() && right.isPrimitive()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;
      if (("long".equals(unbox(leftResult.print())) && rightResult.isIntegralType()) ||
          ("long".equals(unbox(rightResult.print())) && rightResult.isIntegralType())) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        //no part of the expression is a long -> if both parts are integral types then the result is a int
      }else{
        if (
            ("int".equals(unbox(leftResult.print())) || "char".equals(unbox(leftResult.print())) ||
            "short".equals(unbox(leftResult.print())) || "byte".equals(unbox(leftResult.print()))) &&
            ("int".equals(unbox(rightResult.print())) || "char".equals(unbox(rightResult.print())) ||
                "short".equals(unbox(rightResult.print())) || "byte".equals(unbox(rightResult.print())))
        ) {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }
}
