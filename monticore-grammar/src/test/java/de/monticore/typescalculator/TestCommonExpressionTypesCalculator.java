package de.monticore.typescalculator;

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
}
