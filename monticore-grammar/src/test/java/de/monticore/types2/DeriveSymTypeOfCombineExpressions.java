/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.Optional;

public class DeriveSymTypeOfCombineExpressions extends CombineExpressionsWithLiteralsDelegatorVisitor implements ITypesCalculator {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private LastResult lastResult = new LastResult();


  public DeriveSymTypeOfCombineExpressions(IExpressionsBasisScope scope){
    this.realThis=this;

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setScope(scope);
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setScope(scope);
    deriveSymTypeOfAssignmentExpressions.setLastResult(lastResult);
    setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setScope(scope);
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setScope(scope);
    deriveSymTypeOfExpression.setLastResult(lastResult);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    deriveSymTypeOfLiterals.setResult(lastResult);
    this.deriveSymTypeOfLiterals = deriveSymTypeOfLiterals;

    DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    this.deriveSymTypeOfMCCommonLiterals =commonLiteralsTypesCalculator;
    commonLiteralsTypesCalculator.setResult(lastResult);
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    Optional<SymTypeExpression> result = lastResult.getLastOpt();
    lastResult.setLastOpt(Optional.empty());
    return result;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public void setLastResult(LastResult lastResult){
    deriveSymTypeOfAssignmentExpressions.setLastResult(lastResult);
    deriveSymTypeOfMCCommonLiterals.setResult(lastResult);
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    deriveSymTypeOfExpression.setLastResult(lastResult);
    deriveSymTypeOfLiterals.setResult(lastResult);
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
  }

  public void setScope(IExpressionsBasisScope scope){
    deriveSymTypeOfAssignmentExpressions.setScope(scope);
    deriveSymTypeOfExpression.setScope(scope);
    deriveSymTypeOfCommonExpressions.setScope(scope);
    deriveSymTypeOfBitExpressions.setScope(scope);
  }

  @Override
  public void init() {
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    setLastResult(lastResult);
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return lastResult.getLastOpt();
  }
}
