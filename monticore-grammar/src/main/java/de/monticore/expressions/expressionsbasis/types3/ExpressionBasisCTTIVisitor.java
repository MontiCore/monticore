/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

/**
 * Extends with support for
 * additional compile time type inference (generics)
 */
public class ExpressionBasisCTTIVisitor
    extends ExpressionBasisTypeVisitor
    implements ExpressionsBasisHandler {

  /**
   * Resolves using the name and handles compile-time type inference.
   * When using this, {@link #visit(ASTNameExpression)} and
   * {@link #endVisit(ASTNameExpression)} will NOT be called.
   */
  @Override
  public void handle(ASTNameExpression expr) {
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      return;
    }
    SymTypeExpression resolved = getInferenceContext4Ast()
        .getResolvedOfExpression(
            expr, () -> calculateNameExpressionOrLogError(expr)
        ).orElseGet(SymTypeExpressionFactory::createObscureType);

    CompileTimeTypeCalculator.handleResolvedType(
        expr, resolved,
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }
}
