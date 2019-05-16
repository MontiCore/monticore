package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private ASTMCType result;

  private ExpressionsBasisScope scope;

  private Map<ASTNode, MCTypeSymbol> types;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;

  private BasicLiteralsTypeCalculator literalsTypeCalculator;


  public CombineExpressionsWithLiteralsTypesCalculator(ExpressionsBasisScope scope){
    this.realThis=this;
    this.scope=scope;
    this.types = new HashMap<>();

    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setLiteralsVisitor(literalsTypeCalculator);
    commonExpressionTypesCalculator.setTypes(types);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setLiteralsVisitor(literalsTypeCalculator);
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setTypes(types);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    expressionsBasisTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setLiteralsVisitor(literalsTypeCalculator);
    expressionsBasisTypesCalculator.setTypes(types);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);

    setMCBasicLiteralsVisitor(literalsTypeCalculator);

    LiteralsTypesCalculator combineExpressionsWithLiteralsTypesCalculator = new LiteralsTypesCalculator();
    combineExpressionsWithLiteralsTypesCalculator.setTypes(types);
    setCombineExpressionsWithLiteralsVisitor(combineExpressionsWithLiteralsTypesCalculator);

  }

  public ASTMCType calculateType(ASTExpression e){
    e.accept(realThis);
    return types.get(e).getASTMCType();
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public ASTMCType getResult(){
    if(assignmentExpressionTypesCalculator.getResult()!=null){
      return assignmentExpressionTypesCalculator.getResult();
    }
    if(commonExpressionTypesCalculator.getResult()!=null){
      return commonExpressionTypesCalculator.getResult();
    }
    if(expressionsBasisTypesCalculator.getResult()!=null){
      return expressionsBasisTypesCalculator.getResult();
    }
    return null;
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
  }

}
