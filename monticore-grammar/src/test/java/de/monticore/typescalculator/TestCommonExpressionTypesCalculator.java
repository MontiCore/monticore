package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTBooleanExpression;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testcommonexpressions._visitor.TestCommonExpressionsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTIntExpression;

import java.util.Map;

public class TestCommonExpressionTypesCalculator extends CommonExpressionTypesCalculator implements TestCommonExpressionsVisitor {

  private Map<ASTExpression, ASTMCType> types;

  private ASTMCType result;

  public TestCommonExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
  }

  @Override
  public void endVisit(ASTIntExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
  }

  @Override
  public void endVisit(ASTDoubleExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
  }

  @Override
  public void endVisit(ASTBooleanExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN));
  }

  public ASTMCType getResult() {
    return super.getResult();
  }

}
