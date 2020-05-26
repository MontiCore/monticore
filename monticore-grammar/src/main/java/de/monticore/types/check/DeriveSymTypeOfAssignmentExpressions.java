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
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0170",expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0171",expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0172", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0173", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0174", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = typeCheckResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0175", expr.get_SourcePositionStart());
    }
  }

  private void calculatePlusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmeticWithString(expr, expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0176", expr.get_SourcePositionStart());
    }
  }

  private void calculateMinusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0177", expr.get_SourcePositionStart());
    }
  }

  private void calculateMultAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0178", expr.get_SourcePositionStart());
    }
  }

  private void calculateDivideAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0179", expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTAssignmentExpression expr) {
    //there has to be a variable on the left side of an assignmentexpression
    expr.getLeft().accept(getRealThis());
    if(typeCheckResult.isPresentLast()){
      if(!typeCheckResult.isField()){
        Log.error("0xA0180 The expression at source position "+expr.get_SourcePositionStart()+" needs to be a field.");
      }
    }else{
      logError("0xA0181", expr.getLeft().get_SourcePositionStart());
    }
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
      Optional<SymTypeExpression> wholeResult = calculateRegularAssignment(expr.getLeft(), expr.getRight());
      if (wholeResult.isPresent()) {
        //store the result of the expression in the last result
        typeCheckResult.setLast(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA0182", expr.get_SourcePositionStart());
      }
    }
  }

  private void calculateAndAssigment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0183", expr.get_SourcePositionStart());
    }
  }

  private void calculateOrAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0184", expr.get_SourcePositionStart());
    }
  }

  private void calculateBinaryXorAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0185", expr.get_SourcePositionStart());
    }
  }

  private void calculateDoubleRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0186", expr.get_SourcePositionStart());
    }
  }

  private void calculateDoubleLeftAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0187", expr.get_SourcePositionStart());
    }
  }

  private void calculateLogicalRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0188", expr.get_SourcePositionStart());
    }
  }

  private void calculateModuloAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0189", expr.get_SourcePositionStart());
    }
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  private Optional<SymTypeExpression> calculateTypeArithmetic(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0172", left.get_SourcePositionStart());
    }
    right.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0191", right.get_SourcePositionStart());
    }
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
  private Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTExpression expr, ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    left.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0192", left.get_SourcePositionStart());
    }
    right.accept(getRealThis());
    if(!typeCheckResult.isPresentLast()){
      //make sure that there is a right result
      logError("0xA0193", right.get_SourcePositionStart());
    }
    //if the type of the left expression is a String then so is the type of the whole expression
    if (isString(leftResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", getScope(expr.getEnclosingScope())));
    }
    //else continue with the normal calculation of +=,-=,*=,/= and %=
    return calculateTypeArithmetic(left, right);
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  private Optional<SymTypeExpression> calculateTypeBitOperation(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0194", left.get_SourcePositionStart());
    }
    right.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0195", right.get_SourcePositionStart());
    }
    //the bitshift operations are only defined for integers --> long, int, char, short, byte
    if (leftResult.isPrimitive() && ((SymTypeConstant) leftResult).isIntegralType() && rightResult
        .isPrimitive() && ((SymTypeConstant) rightResult).isIntegralType()) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  private Optional<SymTypeExpression> calculateTypeBinaryOperations(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0196", left.get_SourcePositionStart());
    }
    right.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0197", right.get_SourcePositionStart());
    }
    if (leftResult.isPrimitive() && ((SymTypeConstant) leftResult).isIntegralType() && rightResult
        .isPrimitive() && ((SymTypeConstant) rightResult).isIntegralType()) {
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
  public Optional<SymTypeExpression> calculateRegularAssignment(ASTExpression left,
                                                                ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0198", left.get_SourcePositionStart());
    }
    right.accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0199", right.get_SourcePositionStart());
    }
    //option one: both are numeric types and are assignable
    if (leftResult.isPrimitive() && ((SymTypeConstant) leftResult).isNumericType() && rightResult
        .isPrimitive() && ((SymTypeConstant) rightResult).isNumericType() && compatible(leftResult, rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if (rightResult.print().equals(leftResult.print()) || isSubtypeOf(rightResult, leftResult)) {
      //option two: none of them are primitive types and they are either from the same class or stand in a super/subtype relation with the supertype on the left side
      return Optional.of(leftResult);
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  private boolean isNumericType(SymTypeExpression ex) {
    return (isDouble(ex) || isFloat(ex) ||
        isLong(ex) || isInt(ex) ||
        isChar(ex) || isShort(ex) ||
        isByte(ex)
    );
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  public static Optional<SymTypeExpression> getUnaryNumericPromotionType(SymTypeExpression type) {
    if (isByte(type) || isShort(type) || isChar(type) || isInt(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    if (isLong(type) || isDouble(type) || isFloat(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(unbox(type.print())));
    }
    return Optional.empty();
  }
}
