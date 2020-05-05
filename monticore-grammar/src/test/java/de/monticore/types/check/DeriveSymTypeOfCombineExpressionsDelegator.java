/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsDelegatorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

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

  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public DeriveSymTypeOfCombineExpressionsDelegator(IDerivePrettyPrinter prettyPrinter){
    this.realThis=this;
    this.prettyPrinter = prettyPrinter;

    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setPrettyPrinter(prettyPrinter);
    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);

    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfAssignmentExpressions.setPrettyPrinter(prettyPrinter);
    setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setPrettyPrinter(prettyPrinter);
    setBitExpressionsVisitor(deriveSymTypeOfBitExpressions);

    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setPrettyPrinter(prettyPrinter);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);

    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions();
    deriveSymTypeOfJavaClassExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfJavaClassExpressions.setPrettyPrinter(prettyPrinter);
    setJavaClassExpressionsVisitor(deriveSymTypeOfJavaClassExpressions);

    deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfSetExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSetExpressions.setPrettyPrinter(prettyPrinter);
    setSetExpressionsVisitor(deriveSymTypeOfSetExpressions);

    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    deriveSymTypeOfLiterals.setResult(typeCheckResult);

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    setMCCommonLiteralsVisitor(commonLiteralsTypesCalculator);
    commonLiteralsTypesCalculator.setResult(typeCheckResult);

    symTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();

    deriveSymTypeOfCombineExpressions = new DeriveSymTypeOfCombineExpressions(symTypeFromMCSimpleGenericTypes);
    deriveSymTypeOfCombineExpressions.setTypeCheckResult(typeCheckResult);
    setCombineExpressionsWithLiteralsVisitor(deriveSymTypeOfCombineExpressions);
  }

  /**
   * main method to calculate the type of an expression
   */
  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (typeCheckResult.isPresentLast()) {
      result = Optional.ofNullable(typeCheckResult.getLast());
    }
    typeCheckResult.reset();
    return result;
  }

  @Override
  public CombineExpressionsWithLiteralsDelegatorVisitor getRealThis(){
    return realThis;
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfMCCommonLiterals.setResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfJavaClassExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSetExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCombineExpressions.setTypeCheckResult(typeCheckResult);
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
    setTypeCheckResult(typeCheckResult);
  }

  /**
   * main method to calculate the type of a literal
   */
  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (typeCheckResult.isPresentLast()) {
      result = Optional.ofNullable(typeCheckResult.getLast());
    }
    typeCheckResult.reset();
    return result;
  }
}
