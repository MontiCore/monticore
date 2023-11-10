/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

/**
 * Delegator Visitor to test the combination of the grammars
 */
public class FullDeriveFromCombineExpressionsWithLiterals extends AbstractDerive {

  private DeriveSymTypeOfAssignmentExpressions deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfJavaClassExpressions deriveSymTypeOfJavaClassExpressions;

  private DeriveSymTypeOfLambdaExpressions deriveSymTypeOfLambdaExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfCombineExpressions deriveSymTypeOfCombineExpressions;

  private DeriveSymTypeOfUglyExpressions deriveSymTypeOfUglyExpressions;

  private FullSynthesizeFromCombineExpressionsWithLiterals synthesizer;

  public FullDeriveFromCombineExpressionsWithLiterals(){
    this(CombineExpressionsWithLiteralsMill.traverser());
  }

  public FullDeriveFromCombineExpressionsWithLiterals(CombineExpressionsWithLiteralsTraverser traverser){
    super(traverser);
    init(traverser);
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfJavaClassExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLambdaExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCombineExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfUglyExpressions.setTypeCheckResult(typeCheckResult);
  }

  /**
   * initialize the typescalculator
   */
  public void init(CombineExpressionsWithLiteralsTraverser traverser) {
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    synthesizer = new FullSynthesizeFromCombineExpressionsWithLiterals();
    deriveSymTypeOfJavaClassExpressions = new DeriveSymTypeOfJavaClassExpressions(synthesizer);
    deriveSymTypeOfLambdaExpressions = new DeriveSymTypeOfLambdaExpressions();
    deriveSymTypeOfLambdaExpressions.setSynthesize(synthesizer);
    deriveSymTypeOfCombineExpressions = new DeriveSymTypeOfCombineExpressions(synthesizer);
    deriveSymTypeOfUglyExpressions = new DeriveSymTypeOfUglyExpressions(synthesizer);
    setTypeCheckResult(getTypeCheckResult());

    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
    traverser.add4JavaClassExpressions(deriveSymTypeOfJavaClassExpressions);
    traverser.setLambdaExpressionsHandler(deriveSymTypeOfLambdaExpressions);
    traverser.add4LambdaExpressions(deriveSymTypeOfLambdaExpressions);
    traverser.setJavaClassExpressionsHandler(deriveSymTypeOfJavaClassExpressions);
    traverser.add4CombineExpressionsWithLiterals(deriveSymTypeOfCombineExpressions);
    traverser.setCombineExpressionsWithLiteralsHandler(deriveSymTypeOfCombineExpressions);
    traverser.add4UglyExpressions(deriveSymTypeOfUglyExpressions);
    traverser.setUglyExpressionsHandler(deriveSymTypeOfUglyExpressions);
  }
}
