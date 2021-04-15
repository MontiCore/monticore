/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.abstracttypechecktest.AbstractTypeCheckTestMill;
import de.monticore.expressions.abstracttypechecktest._visitor.AbstractTypeCheckTestTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

public class DeriveSymTypeOfAbstractTypeCheckDelegator implements IDerive {

  private AbstractTypeCheckTestTraverser traverser;

  private DeriveSymTypeOfBSCommonExpressions deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public DeriveSymTypeOfAbstractTypeCheckDelegator(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }else{
      return Optional.empty();
    }
  }

  @Override
  public AbstractTypeCheckTestTraverser getTraverser(){
    return traverser;
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
  @Override
  public void init() {
    this.traverser = AbstractTypeCheckTestMill.traverser();
    this.typeCheckResult = new TypeCheckResult();
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfBSCommonExpressions();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    setTypeCheckResult(typeCheckResult);

    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);
  }


}
