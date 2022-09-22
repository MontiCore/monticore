/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypePrimitive.unbox;
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
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression calcResult = calculateIncSuffixExpression(innerResult);
      storeResultOrLogError(calcResult, expr, "0xA0170");
    }
  }

  protected SymTypeExpression calculateIncSuffixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression calcResult = calculateDecSuffixExpression(innerResult);
      storeResultOrLogError(calcResult, expr, "0xA0171");
    }
  }

  protected SymTypeExpression calculateDecSuffixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression calcResult = calculateIncPrefixExpression(innerResult);
      storeResultOrLogError(calcResult, expr, "0xA0172");
    }
  }

  protected SymTypeExpression calculateIncPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression calcResult = calculateDecPrefixExpression(innerResult);
      storeResultOrLogError(calcResult, expr, "0xA0173");
    }
  }

  protected SymTypeExpression calculateDecPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  protected boolean checkResultIsField(ASTExpression expr){
    expr.accept(getTraverser());
    return getTypeCheckResult().isPresentResult() && getTypeCheckResult().isField();
  }

  protected void calculatePlusAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeArithmeticWithStringAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0176");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateMinusAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeArithmeticAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0177");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateMultAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeArithmeticAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0178");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateDivideAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeArithmeticAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0179");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void traverse(ASTAssignmentExpression expr) {
    //there has to be a variable on the left side of an assignmentexpression
    calculateAssignmentExpression(expr);
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
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBinaryOperationsAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0183");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateOrAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBinaryOperationsAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0184");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateBinaryXorAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBinaryOperationsAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0185");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateDoubleRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBitOperationAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0186");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateDoubleLeftAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBitOperationAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0187");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateLogicalRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeBitOperationAssignment(innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0188");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected void calculateModuloAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateTypeArithmeticAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0189");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  protected SymTypeExpression calculateTypeArithmeticAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if the left and the right result are a numeric type then the type of the whole expression is the type of the left expression
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(unbox(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method for += because in this case you can use Strings too
   */
  protected SymTypeExpression calculateTypeArithmeticWithStringAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if the type of the left expression is a String then so is the type of the whole expression
    if (isString(leftResult)) {
      return SymTypeExpressionFactory.createTypeObject(leftResult.getTypeInfo());
    }
    //else continue with the normal calculation of +=,-=,*=,/= and %=
    return calculateTypeArithmeticAssignment(expr, leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the type of the bitshift operations (<<=, >>=, >>>=)
   */
  protected SymTypeExpression calculateTypeBitOperationAssignment(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //the bitshift operations are only defined for integers --> long, int, char, short, byte
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(leftResult.print());
    }
    //should not happen, not valid, will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method for the calculation of the type of the binary operations (&=,|=,^=)
   */
  protected SymTypeExpression calculateTypeBinaryOperationsAssignment(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    if (isIntegralType(leftResult) && isIntegralType(rightResult)) {
      //option 1: both are of integral type
      return SymTypeExpressionFactory.createPrimitive(leftResult.print());
    } else if (isBoolean(leftResult) && isBoolean(rightResult)) {
      //option 2: both are booleans
      return SymTypeExpressionFactory.createPrimitive("boolean");
    }
    //should not happen, not valid, will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  protected void calculateRegularAssignment(ASTAssignmentExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)) {
      SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
      if(checkResultIsField(expr.getLeft())) {
        wholeResult = calculateRegularAssignment(expr, innerTypes.get(0), innerTypes.get(1));
      }
      storeResultOrLogError(wholeResult, expr, "0xA0182");
    }else{
      if(expr.getLeft() instanceof ASTNameExpression || expr.getLeft() instanceof ASTFieldAccessExpression){
        if(innerTypes.get(0).isObscureType()){
          Log.error("0xA0181 The left expression of the assignment could not be calculated");
        }
      }
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * helper method for the calculation of a regular assignment (=)
   */
  protected SymTypeExpression calculateRegularAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //option one: both are numeric types and are assignable
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    if (isNumericType(leftResult) && isNumericType(rightResult) && compatible(leftResult, rightResult)) {
      wholeResult = SymTypeExpressionFactory.createTypeExpression(leftResult.getTypeInfo());
    } else if (compatible(leftResult, rightResult)) {
      //option two: none of them are primitive types and they are either from the same class or stand in a super/subtype relation with the supertype on the left side
      wholeResult = leftResult;
    }
    return wholeResult;
  }
}
