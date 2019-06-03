package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

public class TestAssignmentExpressionTypesCalculator extends AssignmentExpressionsWithLiteralsTypesCalculator implements TestAssignmentExpressionsVisitor {

  private TestAssignmentExpressionsVisitor realThis;

  public TestAssignmentExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void setRealThis(TestAssignmentExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public TestAssignmentExpressionsVisitor getRealThis(){
    return realThis;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }

}
