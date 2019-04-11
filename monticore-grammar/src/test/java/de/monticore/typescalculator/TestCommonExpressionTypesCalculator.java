package de.monticore.typescalculator;

import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testcommonexpressions._visitor.TestCommonExpressionsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveTypeBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTIntExpression;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestCommonExpressionTypesCalculator extends CommonExpressionTypesCalculator implements TestCommonExpressionsVisitor {

  private Map<ASTExpression, ASTMCType> types;

  private ASTMCType result;

  public TestCommonExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
  }


  @Override
  public void visit(ASTIntExpression expr){

  }

  @Override
  public void endVisit(ASTIntExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
  }

  @Override
  public void visit(ASTDoubleExpression expr){

  }

  @Override
  public void endVisit(ASTDoubleExpression expr){
    types.put(expr,new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
  }

  public ASTMCType getResult() {
    return super.getResult();
  }

}
