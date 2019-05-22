package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressions._visitor.CombineExpressionsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;

public class CombineExpressionsTypesCalculator extends CombineExpressionsDelegatorVisitor {

  private CombineExpressionsDelegatorVisitor realThis;

  private LiteralTypeCalculator literalsVisitor;

  private Map<ASTNode, MCTypeSymbol> types;

  private CommonExpressionsWithLiteralsTypesCalculator commonExpressionsWithLiteralsTypesCalculator;

  private AssignmentExpressionsWithLiteralsTypesCalculator assignmentExpressionsWithLiteralsTypesCalculator;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;


  public CombineExpressionsTypesCalculator(ExpressionsBasisScope scope){
    this.realThis=this;
    this.literalsVisitor=new BasicLiteralsTypeCalculator();
    this.types = new HashMap<>();
    commonExpressionsWithLiteralsTypesCalculator = new CommonExpressionsWithLiteralsTypesCalculator();
    commonExpressionsWithLiteralsTypesCalculator.setScope(scope);
    commonExpressionsWithLiteralsTypesCalculator.setLiteralsVisitor(literalsVisitor);
    commonExpressionsWithLiteralsTypesCalculator.setTypes(types);
    setCommonExpressionsWithLiteralsVisitor(commonExpressionsWithLiteralsTypesCalculator);

    assignmentExpressionsWithLiteralsTypesCalculator = new AssignmentExpressionsWithLiteralsTypesCalculator();
    assignmentExpressionsWithLiteralsTypesCalculator.setLiteralsVisitor(literalsVisitor);
    assignmentExpressionsWithLiteralsTypesCalculator.setScope(scope);
    assignmentExpressionsWithLiteralsTypesCalculator.setTypes(types);
    setAssignmentExpressionsWithLiteralsVisitor(assignmentExpressionsWithLiteralsTypesCalculator);

    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setLiteralsVisitor(literalsVisitor);
    commonExpressionTypesCalculator.setTypes(types);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setLiteralsVisitor(literalsVisitor);
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setTypes(types);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    expressionsBasisTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setLiteralsVisitor(literalsVisitor);
    expressionsBasisTypesCalculator.setTypes(types);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }

  @Override
  public CombineExpressionsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public ASTMCType getResult(){
    if(commonExpressionsWithLiteralsTypesCalculator.getResult()!=null){
      return commonExpressionsWithLiteralsTypesCalculator.getResult();
    }
    if(assignmentExpressionsWithLiteralsTypesCalculator.getResult()!=null){
      return assignmentExpressionsWithLiteralsTypesCalculator.getResult();
    }
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

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor=literalsVisitor;
  }

}
