/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.types.check.*;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

public class FullDeriveFromTypeCheckTestAbstract extends AbstractDerive {

  public FullDeriveFromTypeCheckTestAbstract(){
    this(TypeCheckTestMill.traverser());
  }

  public FullDeriveFromTypeCheckTestAbstract(TypeCheckTestTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(TypeCheckTestTraverser traverser) {
    DeriveSymTypeOfAssignmentExpressions assignmentExpressions = new DeriveSymTypeOfAssignmentExpressions();
    DeriveSymTypeOfExpression expressionsBasis = new DeriveSymTypeOfExpression();
    DeriveSymTypeOfBSCommonExpressions commonExpressions = new DeriveSymTypeOfBSCommonExpressions();
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

}
