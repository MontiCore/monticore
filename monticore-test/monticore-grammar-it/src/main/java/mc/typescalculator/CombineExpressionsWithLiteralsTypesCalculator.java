/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

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
  public TypeCheckResult deriveType(ASTExpression expr) {
    init();
    expr.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult deriveType(ASTLiteral lit) {
    init();
    lit.accept(traverser);
    return typeCheckResult.copy();
  }

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
