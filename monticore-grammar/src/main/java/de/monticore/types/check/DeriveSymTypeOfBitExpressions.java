/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.List;
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
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLeftShiftExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0200");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLeftShiftExpression(ASTLeftShiftExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeShift(right, left);
  }

  @Override
  public void traverse(ASTRightShiftExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateRightShiftExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0201");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateRightShiftExpression(ASTRightShiftExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeShift(right, left);
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLogicalRightShiftExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0202");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLogicalRightShiftExpression(ASTLogicalRightShiftExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeShift(right, left);
  }

  @Override
  public void traverse(ASTBinaryAndExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBinaryAndExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0203");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBinaryAndExpression(ASTBinaryAndExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeBinary(right, left);
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBinaryOrOpExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0204");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBinaryOrOpExpression(ASTBinaryOrOpExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeBinary(right, left);
  }

  @Override
  public void traverse(ASTBinaryXorExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBinaryXorOpExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0205");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBinaryXorOpExpression(ASTBinaryXorExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeBinary(right, left);
  }

  protected SymTypeExpression calculateTypeShift(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on integral type - integral type
      if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return shiftCalculator(leftResult, rightResult);
      }
    }
    //should not happen, will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  protected SymTypeExpression calculateTypeBinary(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (TypeCheck.isBoolean(leftResult) && TypeCheck.isBoolean(rightResult)) {
        return SymTypeExpressionFactory.createPrimitive("boolean");
      } else if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return getBinaryNumericPromotion(leftResult, rightResult);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  protected SymTypeExpression shiftCalculator(SymTypeExpression left, SymTypeExpression right) {
    if (!left.isPrimitive() || !right.isPrimitive()){
      return SymTypeExpressionFactory.createObscureType();
    }
    SymTypePrimitive leftResult = (SymTypePrimitive) left;
    SymTypePrimitive rightResult = (SymTypePrimitive) right;

    //only defined on integral type - integral type
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      if (TypeCheck.isLong(rightResult)) {
        if (TypeCheck.isLong(leftResult)) {
          return SymTypeExpressionFactory.createPrimitive("long");
        } else {
          return SymTypeExpressionFactory.createPrimitive("int");
        }
      } else {
        return SymTypeExpressionFactory.createPrimitive("int");
      }
    }
    //should never happen
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method to calculate the type of the BinaryExpressions
   */
  protected SymTypeExpression getBinaryNumericPromotion(SymTypeExpression left, SymTypeExpression right){
    //only integral type - integral type
    if(left.isPrimitive() && right.isPrimitive()) {
      SymTypePrimitive leftResult = (SymTypePrimitive) left;
      SymTypePrimitive rightResult = (SymTypePrimitive) right;
      if ((TypeCheck.isLong(leftResult) && rightResult.isIntegralType()) ||
          (TypeCheck.isLong(rightResult) && leftResult.isIntegralType())) {
        return SymTypeExpressionFactory.createPrimitive("long");
        //no part of the expression is a long -> if both parts are integral types then the result is a int
      }else{
        if (leftResult.isIntegralType()&&rightResult.isIntegralType()) {
          return SymTypeExpressionFactory.createPrimitive("int");
        }
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }
}
