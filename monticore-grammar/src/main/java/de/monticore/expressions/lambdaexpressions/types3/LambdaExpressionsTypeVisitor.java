/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.lambdaexpressions.types3;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpressionBody;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;

public class LambdaExpressionsTypeVisitor extends AbstractTypeVisitor
    implements LambdaExpressionsVisitor2 {

  @Override
  public void endVisit(ASTLambdaExpression exp) {
    SymTypeExpression returnType = exp.getLambdaBody().getType();

    List<SymTypeExpression> parameters = new LinkedList<>();
    boolean obscureParams = false;
    for (ASTLambdaParameter parameter : exp.getLambdaParameters().getLambdaParameterList()) {
      SymTypeExpression paramSymType = calculateTypeOfLambdaParameter(parameter);
      parameters.add(paramSymType);
      if (paramSymType.isObscureType()) {
        obscureParams = true;
      }
    }

    if (!returnType.isObscureType() && !obscureParams) {
      SymTypeExpression wholeResult =
          SymTypeExpressionFactory.createFunction(returnType, parameters);
      getType4Ast().setTypeOfExpression(exp, wholeResult);
    }
    else {
      getType4Ast().setTypeOfExpression(exp, SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void endVisit(ASTLambdaExpressionBody body) {
    body.setType(getType4Ast().getPartialTypeOfExpr(body.getExpression()));
  }

  protected SymTypeExpression calculateTypeOfLambdaParameter(ASTLambdaParameter parameter) {
    if (parameter.isPresentMCType()) {
      return getType4Ast().getPartialTypeOfTypeId(parameter.getMCType());
    }
    Log.error("0xBC373 unable to calculate type of lambda parameter",
        parameter.get_SourcePositionStart(), parameter.get_SourcePositionEnd());
    return SymTypeExpressionFactory.createObscureType();
  }
}
