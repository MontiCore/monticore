package de.monticore.types2;

import de.monticore.expressions.assignmentexpressions._ast.ASTRegularAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.typescalculator.TypesCalculatorHelper.isNumericType;

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
    result = calculateTypeOfRegularAssignment(leftResult,rightResult);
  }

  /**
   * helper method for traverse method
   * calculates the type of the whole regular expression based on the types of the left and right expression
   */
  public Optional<SymTypeExpression> calculateTypeOfRegularAssignment(SymTypeExpression left, SymTypeExpression right){
    // only basic int - int for testing, anything else will be added later
    if(left.print().equals("int")&&right.print().equals("int")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }
}
