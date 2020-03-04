/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator extends CombineExpressionsWithLiteralsDelegatorVisitor implements ITypesCalculator {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private DeriveSymTypeOfAssignmentExpressions assignmentExpressionTypesCalculator;

  private DeriveSymTypeOfCommonExpressions commonExpressionTypesCalculator;

  private DeriveSymTypeOfExpression expressionsBasisTypesCalculator;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  private LastResult lastResult = new LastResult();


  public CombineExpressionsWithLiteralsTypesCalculator(IExpressionsBasisScope scope){
    this.realThis=this;
    commonExpressionTypesCalculator = new DeriveSymTypeOfCommonExpressions();
    commonExpressionTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setLastResult(lastResult);
    setCommonExpressionsVisitor(commonExpressionTypesCalculator);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setScope(scope);
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    assignmentExpressionTypesCalculator = new DeriveSymTypeOfAssignmentExpressions();
    assignmentExpressionTypesCalculator.setScope(scope);
    assignmentExpressionTypesCalculator.setLastResult(lastResult);
    setAssignmentExpressionsVisitor(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new DeriveSymTypeOfExpression();
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

    setLastResult(lastResult);
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    Optional<SymTypeExpression> last = Optional.empty();
    if (lastResult.isPresentLast()) {
      last = Optional.ofNullable(lastResult.getLast());
    }
    lastResult.reset();
    return last;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> last = Optional.empty();
    if (lastResult.isPresentLast()) {
      last = Optional.ofNullable(lastResult.getLast());
    }
    lastResult.reset();
    return last;
  }

  @Override
  public void init() {
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    commonExpressionTypesCalculator = new DeriveSymTypeOfCommonExpressions();
    assignmentExpressionTypesCalculator = new DeriveSymTypeOfAssignmentExpressions();
    expressionsBasisTypesCalculator = new DeriveSymTypeOfExpression();
    setLastResult(lastResult);
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  public void setScope(IExpressionsBasisScope scope){
    assignmentExpressionTypesCalculator.setScope(scope);
    expressionsBasisTypesCalculator.setScope(scope);
    commonExpressionTypesCalculator.setScope(scope);
    deriveSymTypeOfBitExpressions.setScope(scope);
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
    assignmentExpressionTypesCalculator.setLastResult(lastResult);
    commonExpressionTypesCalculator.setLastResult(lastResult);
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
    expressionsBasisTypesCalculator.setLastResult(lastResult);
    deriveSymTypeOfLiterals.setResult(lastResult);
    commonLiteralsTypesCalculator.setResult(lastResult);
  }

  public void setPrettyPrinter(IDerivePrettyPrinter prettyPrinter){
    assignmentExpressionTypesCalculator.setPrettyPrinter(prettyPrinter);
    commonExpressionTypesCalculator.setPrettyPrinter(prettyPrinter);
    deriveSymTypeOfBitExpressions.setPrettyPrinter(prettyPrinter);
    expressionsBasisTypesCalculator.setPrettyPrinter(prettyPrinter);
  }
}
