/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.tupleexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.tupleexpressions._ast.ASTTupleExpression;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsHandler;
import de.monticore.expressions.tupleexpressions._visitor.TupleExpressionsTraverser;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;

public class TupleExpressionsCTTIVisitor extends TupleExpressionsTypeVisitor
    implements TupleExpressionsHandler {

  // handler

  protected TupleExpressionsTraverser traverser;

  @Override
  public TupleExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(TupleExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  // CTTI

  @Override
  public void handle(ASTTupleExpression expr) {
    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast()
          .getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getTupleExpressionFunc(expr.sizeExpressions());
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc,
        expr.getExpressionList(),
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
    if (getType4Ast().hasPartialTypeOfExpression(expr) &&
        !getType4Ast().getPartialTypeOfExpr(expr).isObscureType()
    ) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

  // Helper

  /** {@code <T1,T2> (T1,T2) -> (T1,T2)} */
  protected SymTypeOfFunction getTupleExpressionFunc(int n) {
    List<SymTypeInferenceVariable> typeVars =
        Stream.generate(SymTypeExpressionFactory::createInferenceVariable)
            .limit(n)
            .collect(Collectors.toList());
    SymTypeOfFunction tupleExprFunc =
        createFunction(createTuple(typeVars), typeVars);
    return tupleExprFunc;
  }
}
