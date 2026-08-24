/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeCheck3NameHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExpressionBasisTypeVisitor extends AbstractTypeVisitor
    implements ExpressionsBasisVisitor2,
    ExpressionsBasisHandler {

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
   * note: this will not be called in a ASTFieldAccessExpression
   * given the default ASTFieldAccessExpression traversal;
   * given expr. a.b.c, "a" is a ASTNameExpression,
   * however, in FieldAccessExpressions, the "a" is not required to have a type,
   * as such the traversal is customized.
   * Thus, here an expression type has to be calculated.
   */
  @Override
  public void handle(ASTNameExpression expr) {
    // check if inference already calculated something
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      return;
    }
    Optional<SymTypeExpression> resolved =
        calculateNameExpressionOrLogError(expr);
    handleResolvedType(
        expr,
        resolved.orElseGet(SymTypeExpressionFactory::createObscureType)
    );
  }

  protected Optional<SymTypeExpression> calculateNameExpressionOrLogError(
      ASTNameExpression expr
  ) {
    TypeCheck3NameHandler.TypeCheck3NameHandlerResult result =
        TypeCheck3NameHandler.handleName(
            List.of(expr.getName()),
            Collections.emptyList(),
            getAsBasicSymbolsScope(expr.getEnclosingScope()),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
    return result.getExprTypeOfLastNamePart();
  }

  /**
   * generics hookpoint
   */
  protected void handleResolvedType(
      ASTExpression expr,
      SymTypeExpression resolvedType
  ) {
    getType4Ast().setTypeOfExpression(expr, resolvedType);
  }

  @Override
  public void endVisit(ASTLiteralExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        getType4Ast().getPartialTypeOfExpr(expr.getLiteral())
    );
  }
}
