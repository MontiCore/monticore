/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.List;

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
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, "<<");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, ">>");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTLogicalRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveShift(expr, ">>>");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  public SymTypeExpression deriveShift(ASTShiftExpression expr, String op) {
    // calculate the type of inner expressions
    this.getTypeCheckResult().reset();
    expr.getLeft().accept(this.getTraverser());
    TypeCheckResult left = this.getTypeCheckResult().copy();
    this.getTypeCheckResult().reset();
    expr.getRight().accept(this.getTraverser());
    TypeCheckResult right = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!left.isPresentResult() || !right.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0200", expr.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (left.getResult().isObscureType() || right.getResult().isObscureType()) {
      // if left or right obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else {
      return calculateTypeShift(left.getResult(), right.getResult(), op, expr.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression calculateTypeShift(SymTypeExpression leftResult, SymTypeExpression rightResult, String op, SourcePosition pos) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on integral type - integral type
      if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return shiftCalculator(leftResult, rightResult, op, pos);
      }
    }
    //should not happen, will be handled in traverse
    Log.error("0xA0201 Operator " + op + " not applicable to the types" +
      "'" + leftResult.print() + "', '" + rightResult.print() + "'");
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method to calculate the type of the ShiftExpressions
   * cannot be linked with the BinaryExpressions because they are not calculated the same way
   */
  protected SymTypeExpression shiftCalculator(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
    if (!left.isPrimitive() || !right.isPrimitive()){
      Log.error("0xA0204 The operator " + op + " is only applicable to primitive types.", pos);
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
    Log.error("0xA0205 The operator " + op + " is only applicable to integral types.", pos);
    return SymTypeExpressionFactory.createObscureType();
  }

  @Override
  public void traverse(ASTBinaryAndExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr.getLeft(), expr.getRight(), "&");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTBinaryOrOpExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr.getLeft(), expr.getRight(), "|");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTBinaryXorExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.deriveBinary(expr.getLeft(), expr.getRight(), "^");

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression deriveBinary(ASTExpression left, ASTExpression right, String operator) {
    // calculate the type of inner expressions
    this.getTypeCheckResult().reset();
    left.accept(this.getTraverser());
    TypeCheckResult leftRes = this.getTypeCheckResult().copy();
    this.getTypeCheckResult().reset();
    right.accept(this.getTraverser());
    TypeCheckResult rightRes = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!leftRes.isPresentResult() || !rightRes.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0202", left.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (leftRes.getResult().isObscureType() || rightRes.getResult().isObscureType()) {
      // if left or right obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else {
      return calculateTypeBinary(leftRes.getResult(), rightRes.getResult(), operator, left.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression calculateTypeBinary(SymTypeExpression leftResult, SymTypeExpression rightResult, String operator, SourcePosition pos) {
    if (leftResult.isPrimitive() && rightResult.isPrimitive()) {
      SymTypePrimitive leftEx = (SymTypePrimitive) leftResult;
      SymTypePrimitive rightEx = (SymTypePrimitive) rightResult;

      //only defined on boolean - boolean and integral type - integral type
      if (TypeCheck.isBoolean(leftResult) && TypeCheck.isBoolean(rightResult)) {
        return SymTypeExpressionFactory.createPrimitive("boolean");
      } else if (isIntegralType(leftEx) && isIntegralType(rightEx)) {
        return getBinaryNumericPromotion(leftResult, rightResult, operator, pos);
      }
    }
    //should not happen, no valid result, error will be handled in traverse
    Log.error("0xA0203 The operator " + operator + " is not applicable to the types" +
      "'" + leftResult + "', '" + rightResult +"'", pos);
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method to calculate the type of the BinaryExpressions
   */
  protected SymTypeExpression getBinaryNumericPromotion(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos){
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
    Log.error("0xA0206 The types " + left.print() + " and " + right.print() +
      " could not be combined in terms of the operator " + op, pos);
    return SymTypeExpressionFactory.createObscureType();
  }
}
