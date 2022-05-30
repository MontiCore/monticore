/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in AssignmentExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfAssignmentExpressions extends AbstractDeriveFromExpression implements AssignmentExpressionsVisitor2, AssignmentExpressionsHandler {

  protected AssignmentExpressionsTraverser traverser;

  @Override
  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void traverse(ASTIncSuffixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateIncSuffixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0170");
  }

  protected Optional<SymTypeExpression> calculateIncSuffixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateDecSuffixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0171");
  }

  protected Optional<SymTypeExpression> calculateDecSuffixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateIncPrefixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0172");
  }

  protected Optional<SymTypeExpression> calculateIncPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateDecPrefixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0173");
  }

  protected Optional<SymTypeExpression> calculateDecPrefixExpression(SymTypeExpression innerResult) {
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
    if (getTypeCheckResult().isPresentResult()) {
      if (!getTypeCheckResult().isField()) {
        getTypeCheckResult().reset();
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
    } else if (expr.getOperator() == ASTConstantsAssignmentExpressions.AND_EQUALS) {
      calculateAndAssignment(expr);
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

  protected void calculateAndAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0183");
  }

  protected void calculateOrAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0184");
  }

  protected void calculateBinaryXorAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0185");
  }

  protected void calculateDoubleRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0186");
  }

  protected void calculateDoubleLeftAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0187");
  }

  protected void calculateLogicalRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0188");
  }

  protected void calculateModuloAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr, expr.getLeft(), expr.getRight());
    storeResultOrLogError(wholeResult, expr, "0xA0189");
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  protected Optional<SymTypeExpression> calculateTypeArithmetic(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0190");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0191");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeArithmetic(expr, leftResult.get(), rightResult.get());
    } else {
      getTypeCheckResult().reset();
      return Optional.empty();
    }
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
    getTypeCheckResult().reset();
    return Optional.empty();
  }

  /**
   * helper method for += because in this case you can use Strings too
   */
  protected Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTAssignmentExpression expr, ASTExpression left, ASTExpression right) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0192");
    //make sure that there is a right result
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0193");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeArithmeticWithString(expr, leftResult.get(), rightResult.get());
    } else {
      getTypeCheckResult().reset();
      return Optional.empty();
    }
  }

  /**
   * helper method for += because in this case you can use Strings too
   */
  protected Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if the type of the left expression is a String then so is the type of the whole expression
    if (isString(leftResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject(leftResult.getTypeInfo()));
    }
    //else continue with the normal calculation of +=,-=,*=,/= and %=
    return calculateTypeArithmetic(expr, leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  protected Optional<SymTypeExpression> calculateTypeBitOperation(ASTExpression left, ASTExpression right) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0194");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0195");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeBitOperation(leftResult.get(), rightResult.get());
    } else {
      getTypeCheckResult().reset();
      return Optional.empty();
    }
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  protected Optional<SymTypeExpression> calculateTypeBitOperation(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //the bitshift operations are only defined for integers --> long, int, char, short, byte
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    getTypeCheckResult().reset();
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  protected Optional<SymTypeExpression> calculateTypeBinaryOperations(ASTExpression left, ASTExpression right) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0196");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0197");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeBinaryOperations(leftResult.get(), rightResult.get());
    } else {
      getTypeCheckResult().reset();
      return Optional.empty();
    }
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  protected Optional<SymTypeExpression> calculateTypeBinaryOperations(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      //option 1: both are of integral type
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if (isBoolean(leftResult) && isBoolean(rightResult)) {
      //option 2: both are booleans
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should not happen, not valid, will be handled in traverse
    getTypeCheckResult().reset();
    return Optional.empty();
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  protected void calculateRegularAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0198");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0199");
    Optional<SymTypeExpression> wholeResult = (leftResult.isPresent() && rightResult.isPresent()) ?
      calculateRegularAssignment(expr, leftResult.get(), rightResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0182");
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  protected Optional<SymTypeExpression> calculateRegularAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //option one: both are numeric types and are assignable
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isNumericType(leftResult) && isNumericType(rightResult) && compatible(leftResult, rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeExpression(leftResult.getTypeInfo()));
    } else if (compatible(leftResult, rightResult)) {
      //option two: none of them are primitive types and they are either from the same class or stand in a super/subtype relation with the supertype on the left side
      wholeResult = Optional.of(leftResult);
    }
    return wholeResult;
  }
}
