/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.Optional;

/**
 * Extends with support for
 * additional compile time type inference (generics)
 */
public class ExpressionBasisCTTIVisitor
    extends ExpressionBasisTypeVisitor
    implements ExpressionsBasisHandler {

  protected ExpressionsBasisTraverser traverser;

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }

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
    Optional<SymTypeExpression> potentialType = calculateNameExpressionOrLogError(expr);
    if (potentialType.isEmpty()) {
      getType4Ast().setTypeOfExpression(expr,
          SymTypeExpressionFactory.createObscureType()
      );
    }
    else {
      CompileTimeTypeCalculator.handleResolvedType(
          expr, potentialType.get(),
          getTraverser(), getType4Ast(), getInferenceContext4Ast()
      );
    }
  }

}
