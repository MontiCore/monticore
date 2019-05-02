package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testassignmentexpressions._ast.*;
import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

import java.util.Map;
import java.util.Optional;

public class TestAssignmentExpressionTypesCalculator extends AssignmentExpressionTypesCalculator implements TestAssignmentExpressionsVisitor {
  private Map<ASTNode, MCTypeSymbol> types;

  private ASTMCType result;

  private LiteralTypeCalculator literalsVisitor;

  private ExpressionsBasisScope scope;

  public TestAssignmentExpressionTypesCalculator(){
    types=getTypes();
    result=getResult();
    scope=getScope();
    literalsVisitor=getLiteralsVisitor();
  }

  @Override
  public void endVisit(ASTNameExpression expr){
    Optional<EVariableSymbol> var = scope.resolveEVariable(expr.getName());
    MCTypeSymbol sym = var.get().getMCTypeSymbol();
    types.put(expr,sym);
  }

  @Override
  public void endVisit(ASTEExtLiteral expr){
    ASTMCType type = literalsVisitor.calculateType(expr.getLiteral());
    MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
    sym.setASTMCType(type);
    types.put(expr,sym);
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
    super.setScope(scope);
  }

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor=literalsVisitor;
    super.setLiteralsVisitor(literalsVisitor);
  }
}
