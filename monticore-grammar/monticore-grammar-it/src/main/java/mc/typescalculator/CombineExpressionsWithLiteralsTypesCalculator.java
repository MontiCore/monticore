/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.DeriveSymTypeOfLiterals;
import de.monticore.types2.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.*;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements IExpressionAndLiteralsTypeCalculatorVisitor {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private Map<ASTNode, SymTypeExpression> types;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;

  private CombineExpressionsWithLiteralsLiteralTypesCalculator literalsLiteralTypesCalculator;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  private LastResult lastResult = new LastResult();


  public CombineExpressionsWithLiteralsTypesCalculator(IExpressionsBasisScope scope){
    this.realThis=this;
    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setLastResult(lastResult);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setLastResult(lastResult);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    expressionsBasisTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setLastResult(lastResult);
    setExpressionsBasisVisitor(expressionsBasisTypesCalculator);
  
    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setResult(lastResult);
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    this.deriveSymTypeOfLiterals = deriveSymTypeOfLiterals;

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    commonExpressionTypesCalculator.setLastResult(lastResult);
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    this.commonLiteralsTypesCalculator=commonLiteralsTypesCalculator;
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    if(types.get(e)!=null){
      return Optional.of(types.get(e));
    }
    return null;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public Map<ASTNode,SymTypeExpression> getTypes(){
    return types;
  }

  public void setScope(IExpressionsBasisScope scope){
    assignmentExpressionTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setScope(scope);
  }
}
