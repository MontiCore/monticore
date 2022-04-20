/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.Optional;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in BitExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfBitExpressions extends AbstractDeriveFromExpression implements BitExpressionsVisitor2, BitExpressionsHandler {

  protected BitExpressionsTraverser traverser;

  @Override
  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void traverse(ASTLeftShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLeftShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0200");
  }

  protected Optional<SymTypeExpression> calculateLeftShiftExpression(ASTLeftShiftExpression expr) {
    return calculateTypeShift(expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTRightShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateRightShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0201");
  }

  protected Optional<SymTypeExpression> calculateRightShiftExpression(ASTRightShiftExpression expr) {
    return calculateTypeShift(expr.getRight(), expr.getLeft());
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLogicalRightShiftExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0202");
  }

  protected Optional<SymTypeExpression> calculateLogicalRightShiftExpression(ASTLogicalRightShiftExpression expr) {
    return calculateTypeShift(expr.getRight(), expr.getLeft());
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
  protected Optional<SymTypeExpression> calculateTypeShift(ASTExpression right, ASTExpression left) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0206");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0207");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeShift(leftResult.get(), rightResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
  }

  protected Optional<SymTypeExpression> calculateTypeShift(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isTypeConstant() && rightResult.isTypeConstant()) {
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on integral type - integral type
      if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return shiftCalculator(leftResult, rightResult);
      }
    }
    //should not happen, will be handled in traverse
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the BinaryExpressions
   */
  protected Optional<SymTypeExpression> calculateTypeBinary(ASTExpression left, ASTExpression right) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0208");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0209");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeBinary(leftResult.get(), rightResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
  }

  protected Optional<SymTypeExpression> calculateTypeBinary(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isTypeConstant() && rightResult.isTypeConstant()) {
      SymTypeConstant leftEx = (SymTypeConstant) leftResult;
      SymTypeConstant rightEx = (SymTypeConstant) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (TypeCheck.isBoolean(leftResult) && TypeCheck.isBoolean(rightResult)) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
      } else if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return getBinaryNumericPromotion(leftResult, rightResult);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  protected Optional<SymTypeExpression> shiftCalculator(SymTypeExpression left, SymTypeExpression right) {
    if (!left.isTypeConstant() || !right.isTypeConstant()){
      typeCheckResult.reset();
      return Optional.empty();
    }
    SymTypeConstant leftResult = (SymTypeConstant) left;
    SymTypeConstant rightResult = (SymTypeConstant) right;

    //only defined on integral type - integral type
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      if (TypeCheck.isLong(rightResult)) {
        if (TypeCheck.isLong(leftResult)) {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        } else {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      } else {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
      }
    }
    //should never happen
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * helper method to calculate the type of the BinaryExpressions
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotion(SymTypeExpression left, SymTypeExpression right){
    //only integral type - integral type
    if(left.isTypeConstant() && right.isTypeConstant()) {
      SymTypeConstant leftResult = (SymTypeConstant) left;
      SymTypeConstant rightResult = (SymTypeConstant) right;
      if ((TypeCheck.isLong(leftResult) && rightResult.isIntegralType()) ||
          (TypeCheck.isLong(rightResult) && leftResult.isIntegralType())) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
        //no part of the expression is a long -> if both parts are integral types then the result is a int
      }else{
        if (leftResult.isIntegralType()&&rightResult.isIntegralType()) {
          return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
        }
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    typeCheckResult.reset();
    return Optional.empty();
  }
}
