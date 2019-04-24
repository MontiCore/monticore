package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTBooleanExpression;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTIntExpression;
import de.monticore.typescalculator.testassignmentexpressions._ast.ASTNameExpression;
import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

import java.util.Map;
import java.util.Optional;

public class TestAssignmentExpressionTypesCalculator extends AssignmentExpressionTypesCalculator implements TestAssignmentExpressionsVisitor {
  private Map<ASTExpression, MCTypeSymbol> types;

  private ASTMCType result;

  private ExpressionsBasisScope scope;

  public TestAssignmentExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
    scope=getScope();
  }

  @Override
  public void endVisit(ASTIntExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("int");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTDoubleExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("double");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTBooleanExpression expr){
    MCTypeSymbol sym = new MCTypeSymbol("boolean");
    sym.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN));
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTNameExpression expr){
    Optional<EVariableSymbol> var = scope.resolveEVariable(expr.getName());
    MCTypeSymbol sym = var.get().getMCTypeSymbol();
    types.put(expr,sym);
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
    super.setScope(scope);
  }
}
