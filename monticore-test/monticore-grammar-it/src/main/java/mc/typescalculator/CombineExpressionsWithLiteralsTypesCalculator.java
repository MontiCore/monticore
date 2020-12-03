/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import mc.typescalculator.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;

import java.util.Optional;

public class CombineExpressionsWithLiteralsTypesCalculator implements ITypesCalculator {

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

  public Optional<SymTypeExpression> calculateType(ASTExpression e){
    e.accept(traverser);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(traverser);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    lit.accept(traverser);
    Optional<SymTypeExpression> last = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      last = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.reset();
    return last;
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
