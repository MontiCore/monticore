/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.Optional;

import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in BitExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
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
  public void traverse(ASTLeftShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLeftShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0200");
  }

  protected Optional<SymTypeExpression> calculateLeftShiftExpression(ASTLeftShiftExpression expr) {
    return calculateTypeShift(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTRightShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateRightShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0201");
  }

  protected Optional<SymTypeExpression> calculateRightShiftExpression(ASTRightShiftExpression expr) {
    return calculateTypeShift(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLogicalRightShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0202");
  }

  protected Optional<SymTypeExpression> calculateLogicalRightShiftExpression(ASTLogicalRightShiftExpression expr) {
    return calculateTypeShift(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTBinaryAndExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateBinaryAndExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0203");
  }

  protected Optional<SymTypeExpression> calculateBinaryAndExpression(ASTBinaryAndExpression expr) {
    return calculateTypeBinary(expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateBinaryOrOpExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0204");
  }

  protected Optional<SymTypeExpression> calculateBinaryOrOpExpression(ASTBinaryOrOpExpression expr) {
    return calculateTypeBinary(expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTBinaryXorExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateBinaryXorOpExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0205");
  }

  protected Optional<SymTypeExpression> calculateBinaryXorOpExpression(ASTBinaryXorExpression expr) {
    return calculateTypeBinary(expr.getRight(), expr.getLeft());
  }

  /**
   * helper method for the calculation of the type of the ShiftExpressions
   */
  private Optional<SymTypeExpression> calculateTypeShift(ASTShiftExpression expr, ASTExpression right, ASTExpression left) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0206");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0207");
    return calculateTypeShift(expr, leftResult, rightResult);
  }

  protected Optional<SymTypeExpression> calculateTypeShift(ASTShiftExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isTypeConstant() && rightResult.isTypeConstant()) {
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on integral type - integral type
      if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return shiftCalculator(expr, leftResult, rightResult);
      }
    }
    //should not happen, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the BinaryExpressions
   */
  private Optional<SymTypeExpression> calculateTypeBinary(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0208");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0209");
    return calculateTypeBinary(leftResult, rightResult);
  }

  protected Optional<SymTypeExpression> calculateTypeBinary(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isTypeConstant() && rightResult.isTypeConstant()) {
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (isBoolean(leftResult) && isBoolean(rightResult)) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
      } else if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return getBinaryNumericPromotion(leftResult, rightResult);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  protected Optional<SymTypeExpression> shiftCalculator(ASTShiftExpression expr, SymTypeExpression left, SymTypeExpression right) {
    if (left.isTypeConstant() && right.isTypeConstant()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;

      //only defined on integral type - integral type
      if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
        if (isLong(rightResult)) {
          if (isLong(leftResult)) {
            return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
          } else {
            return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
          }
        } else {
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
    if(left.isTypeConstant() && right.isTypeConstant()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;
      if ((isLong(leftResult) && rightResult.isIntegralType()) ||
          (isLong(rightResult) && leftResult.isIntegralType())) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        //no part of the expression is a long -> if both parts are integral types then the result is a int
      }else{
        if (leftResult.isIntegralType()&&rightResult.isIntegralType()) {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }
}
