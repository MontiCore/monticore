/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

import java.util.Optional;

public class DeriveSymTypeFromTypeCheckTest implements ITypesCalculator {

  protected TypeCheckTestTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public DeriveSymTypeFromTypeCheckTest(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression ex) {
    ex.accept(getTraverser());
    return Optional.ofNullable(typeCheckResult.getCurrentResult());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(getTraverser());
    return Optional.ofNullable(typeCheckResult.getCurrentResult());
  }

  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    lit.accept(getTraverser());
    return Optional.ofNullable(typeCheckResult.getCurrentResult());
  }

  @Override
  public void init() {
    typeCheckResult = new TypeCheckResult();
    traverser = TypeCheckTestMill.traverser();

    DeriveSymTypeOfAssignmentExpressions assignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    DeriveSymTypeOfExpression expressionsBasis = new DeriveSymTypeOfExpression();
    DeriveSymTypeOfCommonExpressions commonExpressions = new DeriveSymTypeOfCommonExpressions();
    DeriveSymTypeOfBitExpressions bitExpressions = new DeriveSymTypeOfBitExpressions();
    DeriveSymTypeOfLiterals literalsBasis = new DeriveSymTypeOfLiterals();
    DeriveSymTypeOfMCCommonLiterals commonLiterals = new DeriveSymTypeOfMCCommonLiterals();

    assignmentExpressions.setTypeCheckResult(typeCheckResult);
    expressionsBasis.setTypeCheckResult(typeCheckResult);
    commonExpressions.setTypeCheckResult(typeCheckResult);
    literalsBasis.setTypeCheckResult(typeCheckResult);
    bitExpressions.setTypeCheckResult(typeCheckResult);
    commonLiterals.setTypeCheckResult(typeCheckResult);

    traverser.add4AssignmentExpressions(assignmentExpressions);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);

    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    traverser.add4CommonExpressions(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);

    traverser.add4BitExpressions(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    traverser.add4MCLiteralsBasis(literalsBasis);

    traverser.add4MCCommonLiterals(commonLiterals);
  }

  @Override
  public TypeCheckTestTraverser getTraverser() {
    return traverser;
  }

}
