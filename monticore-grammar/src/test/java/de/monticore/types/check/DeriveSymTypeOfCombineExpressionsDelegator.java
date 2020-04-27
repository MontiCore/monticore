/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

import java.util.Optional;

/**
 * Delegator Visitor to test the combination of the grammars
 */
public class DeriveSymTypeOfCombineExpressionsDelegator extends CombineExpressionsWithLiteralsDelegatorVisitor implements ITypesCalculator {

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

  private DeriveSymTypeOfCombineExpressions deriveSymTypeOfCombineExpressions;

  private SynthesizeSymTypeFromMCSimpleGenericTypes symTypeFromMCSimpleGenericTypes;

  private IDerivePrettyPrinter prettyPrinter;

  private LastResult lastResult = new LastResult();


  public DeriveSymTypeOfCombineExpressionsDelegator(ITypeSymbolsScope scope, IDerivePrettyPrinter prettyPrinter){
    this.realThis=this;
    this.prettyPrinter = prettyPrinter;

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setLastResult(lastResult);
    deriveSymTypeOfCommonExpressions.setPrettyPrinter(prettyPrinter);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setLastResult(lastResult);
    deriveSymTypeOfAssignmentExpressions.setPrettyPrinter(prettyPrinter);
    setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setLastResult(lastResult);
    deriveSymTypeOfBitExpressions.setPrettyPrinter(prettyPrinter);
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setLastResult(lastResult);
    deriveSymTypeOfExpression.setPrettyPrinter(prettyPrinter);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions();
    deriveSymTypeOfJavaClassExpressions.setLastResult(lastResult);
    deriveSymTypeOfJavaClassExpressions.setPrettyPrinter(prettyPrinter);
    setJavaClassExpressionsVisitor(deriveSymTypeOfJavaClassExpressions);

    deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfSetExpressions.setLastResult(lastResult);
    deriveSymTypeOfSetExpressions.setPrettyPrinter(prettyPrinter);
    setSetExpressionsVisitor(deriveSymTypeOfSetExpressions);

    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    deriveSymTypeOfLiterals.setResult(lastResult);

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    commonLiteralsTypesCalculator.setResult(lastResult);

    symTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes(scope);

    deriveSymTypeOfCombineExpressions = new DeriveSymTypeOfCombineExpressions(symTypeFromMCSimpleGenericTypes);
    deriveSymTypeOfCombineExpressions.setLastResult(lastResult);
    setCombineExpressionsWithLiteralsVisitor(deriveSymTypeOfCombineExpressions);
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
    deriveSymTypeOfCombineExpressions.setLastResult(lastResult);
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
    deriveSymTypeOfCombineExpressions = new DeriveSymTypeOfCombineExpressions(symTypeFromMCSimpleGenericTypes);
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
