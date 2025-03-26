/* (c) https://github.com/MontiCore/monticore */
package de.monticore.ocl.oclexpressions.types3;

import de.monticore.ocl.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsHandler;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.List;

public class OCLExpressionsCTTIVisitor
    extends OCLExpressionsTypeVisitor
    implements OCLExpressionsHandler {

  // traverser

  protected OCLExpressionsTraverser traverser;

  @Override
  public OCLExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  // methods

  @Override
  public void handle(ASTIfThenElseExpression expr) {
    CompileTimeTypeCalculator.handlePassThroughExpression(expr,
        List.of(expr.getThenExpression(), expr.getElseExpression()),
        () -> {
          visit(expr);
          traverse(expr);
          endVisit(expr);
        },
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

  @Override
  public void handle(ASTTypeIfExpression expr) {
    CompileTimeTypeCalculator.handlePassThroughExpression(expr,
        List.of(
            expr.getThenExpression().getExpression(),
            expr.getElseExpression()
        ),
        () -> {
          visit(expr);
          traverse(expr);
          endVisit(expr);
        },
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

}
