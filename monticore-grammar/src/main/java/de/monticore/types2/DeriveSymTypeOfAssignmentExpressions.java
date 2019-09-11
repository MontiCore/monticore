package de.monticore.types2;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class DeriveSymTypeOfAssignmentExpressions extends DeriveSymTypeOfExpression implements AssignmentExpressionsVisitor {
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  AssignmentExpressionsVisitor realThis = this;

  @Override
  public void setRealThis(AssignmentExpressionsVisitor realThis){
    this.realThis = realThis;
    super.realThis = realThis;
  }

  @Override
  public AssignmentExpressionsVisitor getRealThis(){
    return realThis;
  }
  // ---------------------------------------------------------- realThis end

  // inherited:
  // public Optional<SymTypeExpression> result = Optional.empty();
  // protected DeriveSymTypeOfLiterals deriveLit;

  // ---------------------------------------------------------- Additional Visting Methods

  /** Overriding the generall error message to see that the error comes from this visitor
   */
  @Override
  public void endVisit(ASTExpression ex){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xEE771 Internal Error: No Type for expression " + ex.toString()
        + ". Probably TypeCheck mis-configured.");
  }

  /**********************************************************************************/

  @Override
  public void traverse(ASTRegularAssignmentExpression ex){
    ex.getLeft().accept(getRealThis());
    //cache result of left Expression
    SymTypeExpression leftResult = getResult().get();
    ex.getRight().accept(getRealThis());
    //cache result of right Expression
    SymTypeExpression rightResult = getResult().get();
    //calculate the type of the whole expression with helper method
    if(ex.getOperator()==ASTConstantsAssignmentExpressions.EQUALS) {
      result = calculateTypeOfRegularAssignment(leftResult, rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.ANDEQUALS){
      result = calculateTypeOfBitwiseAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.PIPEEQUALS){
      result = calculateTypeOfBitwiseAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.ROOFEQUALS){
      result = calculateTypeOfBitwiseAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.GTGTEQUALS){
      result = calculateTypeOfBitshiftAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.GTGTGTEQUALS){
      result = calculateTypeOfBitshiftAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.LTLTEQUALS){
      result = calculateTypeOfBitshiftAssignment(leftResult,rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.MINUSEQUALS){
      result = calculateTypeOfArithmeticAssignment(leftResult, rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.PLUSEQUALS){
      result = calculateTypeOfArithmeticAssignmentWithString(leftResult, rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.SLASHEQUALS){
      result = calculateTypeOfArithmeticAssignment(leftResult, rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.STAREQUALS){
      result = calculateTypeOfArithmeticAssignment(leftResult, rightResult);
    }else if(ex.getOperator()==ASTConstantsAssignmentExpressions.PERCENTEQUALS){
      result = calculateTypeOfArithmeticAssignment(leftResult, rightResult);
    }
    if(!result.isPresent()){
      //log error
    }
  }

  /**
   * helper method for traverse method
   * calculates the type of the whole regular expression based on the types of the left and right expression
   */
  private Optional<SymTypeExpression> calculateTypeOfRegularAssignment(SymTypeExpression left, SymTypeExpression right){
    // only basic int - int for testing, anything else will be added later
    if(left.print().equals("int")&&right.print().equals("int")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }

  @Override
  public void handle(ASTIncPrefixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  @Override
  public void handle(ASTDecPrefixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  @Override
  public void handle(ASTIncSuffixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  @Override
  public void handle(ASTDecSuffixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  @Override
  public void handle(ASTPlusPrefixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  @Override
  public void handle(ASTMinusPrefixExpression ex){
    ex.getExpression().accept(getRealThis());
    SymTypeExpression expression = result.get();
    result = calculateTypeOfUnaryAssignments(expression);
    if(!result.isPresent()){
      //log error
    }
  }

  /**
   * private helper method for the calculation of the unary assignment expressions
   */
  private Optional<SymTypeExpression> calculateTypeOfUnaryAssignments(SymTypeExpression ex){
    if(ex.print().equals("int")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }else if(ex.print().equals("double")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("double"));
    }else if(ex.print().equals("long")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
    }else if(ex.print().equals("float")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("float"));
    }else if(ex.print().equals("short")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("short"));
    }else{
      return Optional.empty();
    }
  }

  private Optional<SymTypeExpression> calculateTypeOfBitwiseAssignment(SymTypeExpression left, SymTypeExpression right){
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeOfBitshiftAssignment(SymTypeExpression left, SymTypeExpression right){
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeOfArithmeticAssignment(SymTypeExpression left, SymTypeExpression right){
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeOfArithmeticAssignmentWithString(SymTypeExpression left, SymTypeExpression right){
    if(left.print().equals("String")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("String"));
    }
    return calculateTypeOfArithmeticAssignment(left,right);
  }
}
