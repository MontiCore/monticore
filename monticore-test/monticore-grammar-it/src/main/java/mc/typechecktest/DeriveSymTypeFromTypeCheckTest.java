/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

import java.util.Optional;

public class DeriveSymTypeFromTypeCheckTest implements IDerive {

  protected TypeCheckTestTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public DeriveSymTypeFromTypeCheckTest(){
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

  public TypeCheckTestTraverser getTraverser() {
    return traverser;
  }

}
