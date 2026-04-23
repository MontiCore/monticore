/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;

/**
 * AssignmentExpressions with additional generics support.
 */
public class AssignmentExpressionsCTTIVisitor
    extends AssignmentExpressionsTypeVisitor
    implements AssignmentExpressionsHandler {

  protected AssignmentExpressionsTraverser traverser;

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTAssignmentExpression expr) {
    getTraverser().visit(expr);
    expr.getLeft().accept(getTraverser());
    setTargetType(expr);
    expr.getRight().accept(getTraverser());
    getTraverser().endVisit(expr);
  }

  protected void setTargetType(ASTAssignmentExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    if (!SymTypeRelations.normalize(left).isObscureType()) {
      getInferenceContext4Ast().setTargetTypeOfExpression(expr.getRight(), left);
    }
  }

}
