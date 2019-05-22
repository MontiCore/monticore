package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.testcommonexpressions._visitor.TestCommonExpressionsVisitor;

public class TestCommonExpressionTypesCalculator extends CommonExpressionsWithLiteralsTypesCalculator implements TestCommonExpressionsVisitor {

  private TestCommonExpressionsVisitor realThis;

  public TestCommonExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void setRealThis(TestCommonExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public TestCommonExpressionsVisitor getRealThis(){
    return realThis;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }
}
