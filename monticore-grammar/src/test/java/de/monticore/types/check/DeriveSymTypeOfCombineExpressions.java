/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Optional;

/**
 * Delegator Visitor to test the combination of the grammars
 */
public class DeriveSymTypeOfCombineExpressions extends CombineExpressionsWithLiteralsDelegatorVisitor implements ITypesCalculator {

  private CombineExpressionsWithLiteralsDelegatorVisitor realThis;

  private DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfJavaClassExpressions deriveSymTypeOfJavaClassExpressions;

  private DeriveSymTypeOfSetExpressions deriveSymTypeOfSetExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  private SynthesizeSymTypeFromMCSimpleGenericTypes symTypeFromMCSimpleGenericTypes;

  private IDerivePrettyPrinter prettyPrinter;

  private LastResult lastResult = new LastResult();


  public DeriveSymTypeOfCombineExpressions(IExpressionsBasisScope scope, IDerivePrettyPrinter prettyPrinter){
    this.realThis=this;
    this.prettyPrinter = prettyPrinter;

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setScope(scope);
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    deriveSymTypeOfCommonExpressions.setPrettyPrinter(prettyPrinter);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setScope(scope);
    deriveSymTypeOfAssignmentExpressions.setLastResult(lastResult);
    deriveSymTypeOfAssignmentExpressions.setPrettyPrinter(prettyPrinter);
    setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setScope(scope);
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
    deriveSymTypeOfBitExpressions.setPrettyPrinter(prettyPrinter);
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setScope(scope);
    deriveSymTypeOfExpression.setLastResult(lastResult);
    deriveSymTypeOfExpression.setPrettyPrinter(prettyPrinter);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions();
    deriveSymTypeOfJavaClassExpressions.setScope(scope);
    deriveSymTypeOfJavaClassExpressions.setLastResult(lastResult);
    deriveSymTypeOfJavaClassExpressions.setPrettyPrinter(prettyPrinter);
    setJavaClassExpressionsVisitor(deriveSymTypeOfJavaClassExpressions);

    deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfSetExpressions.setScope(scope);
    deriveSymTypeOfSetExpressions.setLastResult(lastResult);
    deriveSymTypeOfSetExpressions.setPrettyPrinter(prettyPrinter);
    setSetExpressionsVisitor(deriveSymTypeOfSetExpressions);

    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    deriveSymTypeOfLiterals.setResult(lastResult);

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    commonLiteralsTypesCalculator.setResult(lastResult);

    setScope(scope);
  }

  /**
   * main method to calculate the type of an expression
   */
  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (lastResult.isPresentLast()) {
      result = Optional.ofNullable(lastResult.getLast());
    }
    lastResult.reset();
    return result;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setLastResult(LastResult lastResult){
    deriveSymTypeOfAssignmentExpressions.setLastResult(lastResult);
    deriveSymTypeOfMCCommonLiterals.setResult(lastResult);
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    deriveSymTypeOfExpression.setLastResult(lastResult);
    deriveSymTypeOfLiterals.setResult(lastResult);
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
    deriveSymTypeOfJavaClassExpressions.setLastResult(lastResult);
    deriveSymTypeOfSetExpressions.setLastResult(lastResult);
  }

  /**
   * set the scope of the typescalculator, important for resolving for e.g. NameExpression
   */
  public void setScope(IExpressionsBasisScope scope){
    deriveSymTypeOfAssignmentExpressions.setScope(scope);
    deriveSymTypeOfExpression.setScope(scope);
    deriveSymTypeOfCommonExpressions.setScope(scope);
    deriveSymTypeOfBitExpressions.setScope(scope);
    deriveSymTypeOfJavaClassExpressions.setScope(scope);
    deriveSymTypeOfSetExpressions.setScope(scope);
  }

  /**
   * initialize the typescalculator
   */
  @Override
  public void init() {
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions();
    deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    setLastResult(lastResult);
  }

  /**
   * main method to calculate the type of a literal
   */
  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (lastResult.isPresentLast()) {
      result = Optional.ofNullable(lastResult.getLast());
    }
    lastResult.reset();
    return result;
  }
}
