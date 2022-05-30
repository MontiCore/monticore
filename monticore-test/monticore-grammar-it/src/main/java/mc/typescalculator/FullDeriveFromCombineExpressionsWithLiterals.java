/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

public class FullDeriveFromCombineExpressionsWithLiterals extends AbstractDerive {

  private DeriveSymTypeOfAssignmentExpressions assignmentExpressionTypesCalculator;

  private DeriveSymTypeOfCommonExpressions commonExpressionTypesCalculator;

  private DeriveSymTypeOfExpression expressionsBasisTypesCalculator;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  public FullDeriveFromCombineExpressionsWithLiterals(){
    this(CombineExpressionsWithLiteralsMill.traverser());
  }

  public FullDeriveFromCombineExpressionsWithLiterals(CombineExpressionsWithLiteralsTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(CombineExpressionsWithLiteralsTraverser traverser) {
    commonExpressionTypesCalculator = new DeriveSymTypeOfCommonExpressions();
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    traverser.setCommonExpressionsHandler(commonExpressionTypesCalculator);
    traverser.add4CommonExpressions(commonExpressionTypesCalculator);

    deriveSymTypeOfBitExpressions = new DeriveSymTypeOfBitExpressions();
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    traverser.setBitExpressionsHandler(deriveSymTypeOfBitExpressions);
    traverser.add4BitExpressions(deriveSymTypeOfBitExpressions);

    assignmentExpressionTypesCalculator = new DeriveSymTypeOfAssignmentExpressions();
    assignmentExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    traverser.add4AssignmentExpressions(assignmentExpressionTypesCalculator);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionTypesCalculator);

    expressionsBasisTypesCalculator = new DeriveSymTypeOfExpression();
    expressionsBasisTypesCalculator.setTypeCheckResult(typeCheckResult);
    traverser.setExpressionsBasisHandler(expressionsBasisTypesCalculator);
    traverser.add4ExpressionsBasis(expressionsBasisTypesCalculator);

    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

    commonLiteralsTypesCalculator = new DeriveSymTypeOfMCCommonLiterals();
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCommonLiterals(commonLiteralsTypesCalculator);
  }

  public void setTraverser(CombineExpressionsWithLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    this.typeCheckResult = typeCheckResult;
    assignmentExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    commonExpressionTypesCalculator.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBitExpressions.setTypeCheckResult(typeCheckResult);
    expressionsBasisTypesCalculator.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    commonLiteralsTypesCalculator.setTypeCheckResult(typeCheckResult);
  }
}
