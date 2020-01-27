/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.compatible;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;

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
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0170 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0171 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0172 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0173 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0174 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //get the result of the inner expression
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0175 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculatePlusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmeticWithString(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0176 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateMinusAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0177 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateMultAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0178 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateDivideAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0179 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTAssignmentExpression expr) {
    //there has to be a variable on the left side of an assignmentexpression
    expr.getLeft().accept(getRealThis());
    if(lastResult.isPresentLast()){
      if(!lastResult.isField()){
        Log.error("0xA0180 The resulting type cannot be calculated because the inner left expression is no field");
      }
    }else{
      Log.error("0xA0181 The resulting type of "+prettyPrinter.prettyprint(expr.getLeft())+" cannot be calculated");
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
        Optional<SymTypeExpression> sym = wholeResult;
        lastResult.setLast(sym.get());
        this.result = sym.get();
      } else {
        lastResult.reset();
        Log.error("0xA0182 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
      }
    }
  }

  private void calculateAndAssigment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0183 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateOrAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0184 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateBinaryXorAssignment(ASTAssignmentExpression expr) {
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0185 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateDoubleRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0186 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateDoubleLeftAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0187 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateLogicalRightAssignment(ASTAssignmentExpression expr) {
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0188 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  private void calculateModuloAssignment(ASTAssignmentExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLast(sym.get());
      this.result = sym.get();
    } else {
      lastResult.reset();
      Log.error("0xA0189 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
    }
  }

  /**
   * helper method for the five basic arithmetic assignment operations (+=,-=,*=,/=,%=)
   */
  private Optional<SymTypeExpression> calculateTypeArithmetic(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0190 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    right.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0191 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
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
  private Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    left.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0192 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    right.accept(getRealThis());
    if(!lastResult.isPresentLast()){
      //make sure that there is a right result
      Log.error("0xA0193 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
    }
    //if the type of the left expression is a String then so is the type of the whole expression
    if ("String".equals(leftResult.print())) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", left.getEnclosingScope()));
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
    if (lastResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0194 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    right.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0195 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0196 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    right.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0197 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
    }
    if (leftResult.isPrimitive() && ((SymTypeConstant) leftResult).isIntegralType() && rightResult
        .isPrimitive() && ((SymTypeConstant) rightResult).isIntegralType()) {
      //option 1: both are of integral type
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if ("boolean".equals(unbox(leftResult.print())) && "boolean".equals(unbox(rightResult.print()))) {
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
    if (lastResult.isPresentLast()) {
      //store the result of the left inner expression in a variable
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0198 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    right.accept(getRealThis());
    if (lastResult.isPresentLast()) {
      //store the result of the right inner expression in a variable
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0199 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
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

  public void setLastResult(LastResult lastResult) {
    this.lastResult = lastResult;
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  private boolean isNumericType(SymTypeExpression ex) {
    return ("double".equals(unbox(ex.print())) || "float".equals(unbox(ex.print())) ||
        "long".equals(unbox(ex.print())) || "int".equals(unbox(ex.print())) ||
        "char".equals(unbox(ex.print())) || "short".equals(unbox(ex.print())) ||
        "byte".equals(unbox(ex.print()))
    );
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  public static Optional<SymTypeExpression> getUnaryNumericPromotionType(SymTypeExpression type) {
    if ("byte".equals(SymTypeConstant.unbox(type.print())) || "short".equals(SymTypeConstant.unbox(type.print())) || "char".equals(SymTypeConstant.unbox(type.print())) || "int".equals(SymTypeConstant.unbox(type.print()))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    if ("long".equals(SymTypeConstant.unbox(type.print())) || "double".equals(SymTypeConstant.unbox(type.print())) || "float".equals(SymTypeConstant.unbox(type.print()))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(SymTypeConstant.unbox(type.print())));
    }
    return Optional.empty();
  }

}
