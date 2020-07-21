/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in AssignmentExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfAssignmentExpressions extends DeriveSymTypeOfExpression implements AssignmentExpressionsVisitor {

  private AssignmentExpressionsVisitor realThis;

  @Override
  public void setRealThis(AssignmentExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public AssignmentExpressionsVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfAssignmentExpressions() {
    realThis = this;
  }

  @Override
  public void traverse(ASTIncSuffixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculateIncSuffixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0170");
  }

  protected Optional<SymTypeExpression> calculateIncSuffixExpression(ASTIncSuffixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculateDecSuffixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0171");
  }

  protected Optional<SymTypeExpression> calculateDecSuffixExpression(ASTDecSuffixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculateIncPrefixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0172");
  }

  protected Optional<SymTypeExpression> calculateIncPrefixExpression(ASTIncPrefixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculateDecPrefixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0173");
  }

  protected Optional<SymTypeExpression> calculateDecPrefixExpression(ASTDecPrefixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  protected void calculatePlusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmeticWithString(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0176");
  }

  protected void calculateMinusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0177");
  }

  protected void calculateMultAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0178");
  }

  protected void calculateDivideAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0179");
  }

  @Override
  public void traverse(ASTAssignmentExpression expr) {
    //there has to be a variable on the left side of an assignmentexpression
    acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0181");
    if (typeCheckResult.isPresentCurrentResult()) {
      if (!typeCheckResult.isField()) {
        typeCheckResult.reset();
        Log.error("0xA0180 The expression at Source position "+ expr.getLeft().get_SourcePositionStart()+" must be a field");
      } else {
        calculateAssignmentExpression(expr);
      }
    }
  }

  protected void calculateAssignmentExpression(ASTAssignmentExpression expr) {
    //the regular assignment expression covers all assignment expressions --> differentiate between these
    if (expr.getOperator() == ASTConstantsAssignmentExpressions.PLUSEQUALS) {
      calculatePlusAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.MINUSEQUALS) {
      calculateMinusAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.STAREQUALS) {
      calculateMultAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.SLASHEQUALS) {
      calculateDivideAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.ANDEQUALS) {
      calculateAndAssigment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.PIPEEQUALS) {
      calculateOrAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.GTGTEQUALS) {
      calculateDoubleRightAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.LTLTEQUALS) {
      calculateDoubleLeftAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.GTGTGTEQUALS) {
      calculateLogicalRightAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.ROOFEQUALS) {
      calculateBinaryXorAssignment(expr);
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.PERCENTEQUALS) {
      calculateModuloAssignment(expr);
    } else {
      calculateRegularAssignment(expr);
    }
  }

  protected void calculateAndAssigment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0183");
  }

  protected void calculateOrAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0184");
  }

  protected void calculateBinaryXorAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0185");
  }

  protected void calculateDoubleRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0186");
  }

  protected void calculateDoubleLeftAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0187");
  }

  protected void calculateLogicalRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0188");
  }

  protected void calculateModuloAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0189");
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  private Optional<SymTypeExpression> calculateTypeArithmetic(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0190");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0191");
    return calculateTypeArithmetic(expr, leftResult, rightResult);
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  protected Optional<SymTypeExpression> calculateTypeArithmetic(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if the left and the right result are a numeric type then the type of the whole expression is the type of the left expression
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(unbox(leftResult.print())));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for += because in this case you can use Strings too
   */
  private Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0192");
    //make sure that there is a right result
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0193");
    return calculateTypeArithmeticWithString(expr, leftResult, rightResult);
  }

  /**
   * helper method for += because in this case you can use Strings too
   */
  protected Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if the type of the left expression is a String then so is the type of the whole expression
    if (isString(leftResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", getScope(expr.getEnclosingScope())));
    }
    //else continue with the normal calculation of +=,-=,*=,/= and %=
    return calculateTypeArithmetic(expr, leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  private Optional<SymTypeExpression> calculateTypeBitOperation(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0194");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0195");
    return calculateTypeBitOperation(expr, leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  protected Optional<SymTypeExpression> calculateTypeBitOperation(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //the bitshift operations are only defined for integers --> long, int, char, short, byte
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  private Optional<SymTypeExpression> calculateTypeBinaryOperations(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0196");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0197");
    return calculateTypeBinaryOperations(expr, leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  protected Optional<SymTypeExpression> calculateTypeBinaryOperations(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      //option 1: both are of integral type
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if (isBoolean(leftResult) && isBoolean(rightResult)) {
      //option 2: both are booleans
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  private void calculateRegularAssignment(ASTAssignmentExpression expr) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0198");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0199");
    Optional<SymTypeExpression> wholeResult = calculateRegularAssignment(expr, leftResult, rightResult);
    storeResultOrLogError(wholeResult, expr, "0xA0182");
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  protected Optional<SymTypeExpression> calculateRegularAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //option one: both are numeric types and are assignable
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isNumericType(leftResult) && isNumericType(rightResult) && compatible(leftResult, rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if (compatible(leftResult, rightResult)) {
      //option two: none of them are primitive types and they are either from the same class or stand in a super/subtype relation with the supertype on the left side
      wholeResult = Optional.of(leftResult);
    }
    return wholeResult;
  }

}
