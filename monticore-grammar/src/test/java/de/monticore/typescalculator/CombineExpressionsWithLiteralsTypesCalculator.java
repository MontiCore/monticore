package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.HashMap;
import java.util.Map;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements IExpressionAndLiteralsTypeCalculatorVisitor {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private Map<ASTNode, MCTypeSymbol> types;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;
  
  private CombineExpressionsWithLiteralsLiteralTypesCalculator literalsLiteralTypesCalculator;
  
  private LiteralsBasisTypesCalculator literalsBasisTypesCalculator;
  
  private CommonLiteralsTypesCalculator commonLiteralsTypesCalculator;


  public CombineExpressionsWithLiteralsTypesCalculator(ExpressionsBasisScope scope){
    this.realThis=this;
    this.types = new HashMap<>();

    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setTypes(types);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setTypes(types);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    expressionsBasisTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setTypes(types);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);

    CombineExpressionsWithLiteralsLiteralTypesCalculator literalsLiteralTypesCalculator = new CombineExpressionsWithLiteralsLiteralTypesCalculator();
    literalsLiteralTypesCalculator.setTypes(types);
    setCombineExpressionsWithLiteralsVisitor(literalsLiteralTypesCalculator);
    this.literalsLiteralTypesCalculator=literalsLiteralTypesCalculator;

    LiteralsBasisTypesCalculator literalsBasisTypesCalculator = new LiteralsBasisTypesCalculator();
    setMCLiteralsBasisVisitor(literalsBasisTypesCalculator);
    this.literalsBasisTypesCalculator=literalsBasisTypesCalculator;

    CommonLiteralsTypesCalculator commonLiteralsTypesCalculator = new CommonLiteralsTypesCalculator();
    commonLiteralsTypesCalculator.setTypes(types);
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    this.commonLiteralsTypesCalculator=commonLiteralsTypesCalculator;
  }

  public ASTMCType calculateType(ASTExpression e){
    e.accept(realThis);
    if(types.get(e)!=null){
      return types.get(e).getASTMCType();
    }
    return null;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public Map<ASTNode,MCTypeSymbol> getTypes(){
    return types;
  }

  public void setScope(ExpressionsBasisScope scope){
    assignmentExpressionTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setScope(scope);
  }

}
