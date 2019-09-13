/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types2.DeriveSymTypeOfLiterals;
import de.monticore.types2.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types2.ITypesCalculator;
import de.monticore.types2.SymTypeExpression;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements IExpressionAndLiteralsTypeCalculatorVisitor, ITypesCalculator {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private AssignmentExpressionTypesCalculator assignmentExpressionTypesCalculator;

  private CommonExpressionTypesCalculator commonExpressionTypesCalculator;

  private ExpressionsBasisTypesCalculator expressionsBasisTypesCalculator;

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
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    deriveSymTypeOfLiterals.setResult(lastResult);
    this.deriveSymTypeOfLiterals = deriveSymTypeOfLiterals;

    DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    this.commonLiteralsTypesCalculator=commonLiteralsTypesCalculator;
    commonLiteralsTypesCalculator.setResult(lastResult);
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    return lastResult.getLastOpt();
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public void setLastResult(LastResult lastResult){
    assignmentExpressionTypesCalculator.setLastResult(lastResult);
    commonLiteralsTypesCalculator.setResult(lastResult);
    commonExpressionTypesCalculator.setLastResult(lastResult);
    expressionsBasisTypesCalculator.setLastResult(lastResult);
    deriveSymTypeOfLiterals.setResult(lastResult);
  }

  public void setScope(IExpressionsBasisScope scope){
    assignmentExpressionTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setScope(scope);
  }

  @Override
  public void init() {
    commonExpressionTypesCalculator = new CommonExpressionTypesCalculator();
    assignmentExpressionTypesCalculator = new AssignmentExpressionTypesCalculator();
    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    expressionsBasisTypesCalculator = new ExpressionsBasisTypesCalculator();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setLastResult(lastResult);
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return lastResult.getLastOpt();
  }
}
