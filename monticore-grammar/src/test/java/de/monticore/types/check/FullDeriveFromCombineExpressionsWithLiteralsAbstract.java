/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.abstracttypechecktest.AbstractTypeCheckTestMill;
import de.monticore.expressions.abstracttypechecktest._visitor.AbstractTypeCheckTestTraverser;

public class FullDeriveFromCombineExpressionsWithLiteralsAbstract extends AbstractDerive {

  private DeriveSymTypeOfBSCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  public FullDeriveFromCombineExpressionsWithLiteralsAbstract(){
    this(AbstractTypeCheckTestMill.traverser());
  }

  public FullDeriveFromCombineExpressionsWithLiteralsAbstract(AbstractTypeCheckTestTraverser traverser){
    super(traverser);
    init(traverser);
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
  }

  /**
   * initialize the typescalculator
   */
  public void init(AbstractTypeCheckTestTraverser traverser) {
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfBSCommonExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setTypeCheckResult(getTypeCheckResult());

    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }
}
