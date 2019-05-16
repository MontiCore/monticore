package de.monticore.typescalculator;

import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

import java.util.Map;
import java.util.Optional;

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

}
