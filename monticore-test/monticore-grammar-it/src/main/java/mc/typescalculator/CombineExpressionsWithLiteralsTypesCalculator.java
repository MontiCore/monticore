/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator implements IDerive {

  private CombineExpressionsWithLiteralsTraverser traverser;

  private DeriveSymTypeOfAssignmentExpressions assignmentExpressionTypesCalculator;

  private DeriveSymTypeOfCommonExpressions commonExpressionTypesCalculator;

  private DeriveSymTypeOfExpression expressionsBasisTypesCalculator;

  private DeriveSymTypeOfBitExpressions deriveSymTypeOfBitExpressions;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals commonLiteralsTypesCalculator;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public CombineExpressionsWithLiteralsTypesCalculator(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }

  @Override
  public void init() {
    this.typeCheckResult = new TypeCheckResult();
    this.traverser = CombineExpressionsWithLiteralsMill.traverser();

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

  @Override
  public CombineExpressionsWithLiteralsTraverser getTraverser() {
    return traverser;
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
