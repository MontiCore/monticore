package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTBooleanExpression;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTIntExpression;
import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

import java.util.Map;

public class TestAssignmentExpressionTypesCalculator extends AssignmentExpressionTypesCalculator implements TestAssignmentExpressionsVisitor {
  private Map<ASTExpression, ASTMCType> types;

  private ASTMCType result;

  public TestAssignmentExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
  }

  @Override
  public void endVisit(ASTIntExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
  }

  @Override
  public void endVisit(ASTDoubleExpression expr){
    types.put(expr, new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
  }

  @Override
  public void endVisit(ASTBooleanExpression expr){
    types.put(expr, new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN));
  }
}
