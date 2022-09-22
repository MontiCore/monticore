/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.lambdaexpressions.LambdaExpressionsMill;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._symboltable.ILambdaExpressionsScope;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsHandler;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in LambdaExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfLambdaExpressions extends AbstractDeriveFromExpression
    implements LambdaExpressionsVisitor2, LambdaExpressionsHandler {

  protected LambdaExpressionsTraverser traverser;

  protected AbstractSynthesize synthesize;

  @Override
  public void setTraverser(LambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public LambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  public ISynthesize getSynthesize() {
    return synthesize;
  }

  public void setSynthesize(AbstractSynthesize synthesize) {
    this.synthesize = synthesize;
  }

  @Override
  public void traverse(ASTLambdaExpression exp) {
    getTypeCheckResult().reset();
    exp.getExpression().accept(getTraverser());
    SymTypeExpression returnType = getTypeCheckResult().getResult();

    List<SymTypeExpression> parameters = new LinkedList<>();
    for (ASTLambdaParameter parameter : exp.getLambdaParameters().getLambdaParameterList()) {
      parameters.add(calculateTypeOfLambdaParameter(parameter));
    }

    if(!returnType.isObscureType() && checkNotObscure(parameters)) {
      SymTypeExpression wholeResult =
        SymTypeExpressionFactory.createFunction(returnType, parameters);
      storeResultOrLogError(wholeResult, exp, "A0470");
    } else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  public SymTypeExpression calculateTypeOfLambdaParameter(ASTLambdaParameter parameter) {
    if (parameter.isPresentMCType()) {
      synthesize.getTypeCheckResult().reset();
      parameter.getMCType().accept(synthesize.getTraverser());
      if (synthesize.getTypeCheckResult().isPresentResult()) {
        return synthesize.getTypeCheckResult().getResult();
      }
    }
    Log.error("0xBC373 unable to calculate type of lambda parameter",
        parameter.get_SourcePositionStart(), parameter.get_SourcePositionEnd());
    return SymTypeExpressionFactory.createObscureType();
  }

}
