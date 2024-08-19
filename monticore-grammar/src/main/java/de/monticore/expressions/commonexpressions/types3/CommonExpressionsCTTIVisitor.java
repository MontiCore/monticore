/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.generics.context.InferenceContext;
import de.monticore.types3.generics.context.InferenceResult;
import de.monticore.types3.generics.context.InferenceVisitorMode;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * CommonExpressionsTypeVisitor + generics support
 */
public class CommonExpressionsCTTIVisitor
    extends CommonExpressionsTypeVisitor {

  @Override
  public void handle(ASTConditionalExpression expr) {
    InferenceContext infCtx =
        getInferenceContext4Ast().getContextOfExpression(expr);
    getInferenceContext4Ast().setContextOfExpression(
        expr.getTrueExpression(), infCtx.deepClone()
    );
    getInferenceContext4Ast().setContextOfExpression(
        expr.getFalseExpression(), infCtx.deepClone()
    );

    List<InferenceResult> infRess = new ArrayList<>();
    if (getInferenceContext4Ast().getContextOfExpression(expr)
        .getVisitorMode() != InferenceVisitorMode.TYPE_CHECKING
    ) {
      expr.getTrueExpression().accept(getTraverser());
      expr.getFalseExpression().accept(getTraverser());
      infRess.addAll(getInferenceContext4Ast()
          .getContextOfExpression(expr.getTrueExpression())
          .getInferenceResults()
      );
      infRess.addAll(getInferenceContext4Ast()
          .getContextOfExpression(expr.getFalseExpression())
          .getInferenceResults()
      );
    }

    if (infRess.isEmpty()) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
    else {
      getInferenceContext4Ast().getContextOfExpression(expr)
          .setInferredTypes(infRess);
    }
  }

  @Override
  public void handle(ASTBracketExpression expr) {
    InferenceContext infCtx = getInferenceContext4Ast().getContextOfExpression(expr);
    getInferenceContext4Ast().setContextOfExpression(
        expr.getExpression(), infCtx
    );

    List<InferenceResult> infRess = Collections.emptyList();
    if (infCtx.getVisitorMode() != InferenceVisitorMode.TYPE_CHECKING) {
      expr.getExpression().accept(getTraverser());
      infRess = getInferenceContext4Ast()
          .getContextOfExpression(expr.getExpression()).getInferenceResults();
    }

    if (infRess.isEmpty()) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
    else {
      getInferenceContext4Ast().getContextOfExpression(expr)
          .setInferredTypes(infRess);
    }
  }

  @Override
  public void handle(ASTCallExpression expr) {
    InferenceContext callCtx = getInferenceContext4Ast()
        .getContextOfExpression(expr);
    InferenceVisitorMode mode = callCtx.getVisitorMode();
    boolean hasTargetType = callCtx.hasTargetType();
    // edge case: function returning another function: getFunc()(1);
    if (!hasTargetType) {
      // this is the most information that we have in this context ->
      // if this is not enough,
      // in the current implementation, we cannot calculate the type.
      Optional<SymTypeOfFunction> targetFunc = callCtx
          .getPartialFunctionInfo()
          .getAsFunctionIfComplete();
      if (targetFunc.isPresent()) {
        getInferenceContext4Ast().getContextOfExpression(expr)
            .setTargetType(targetFunc.get());
      }
    }

    visitForInference(expr);
    CompileTimeTypeCalculator.handleCall(expr, expr.getExpression(),
        expr.getArguments().getExpressionList(),
        getTraverser(), getType4Ast(), getInferenceContext4Ast());
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      endVisit(expr);
    }
  }

  /**
   * collects information of the inner expression of the callExpression
   * (which has to be a function), by passing the available information
   * about the function type to the inner expression.
   * The kind of information gathered is
   * dependent on the inference mode the visitor is in.
   */
  protected void visitForInference(ASTCallExpression expr) {
    InferenceContext callCtx = getInferenceContext4Ast().getContextOfExpression(expr);
    InferenceVisitorMode mode = callCtx.getVisitorMode();
    ASTExpression funcExpr = expr.getExpression();
    InferenceContext funcCtx = new InferenceContext();
    funcCtx.setVisitorMode(mode);
    funcCtx.getPartialFunctionInfo().setArgumentExprs(expr.getArguments().getExpressionList());
    if (callCtx.hasTargetType()) {
      funcCtx.getPartialFunctionInfo().setReturnTargetType(callCtx.getTargetType());
    }
    getType4Ast().reset(funcExpr);
    getInferenceContext4Ast().reset(funcExpr);
    getInferenceContext4Ast().setContextOfExpression(funcExpr, funcCtx);
    funcExpr.accept(getTraverser());
    funcCtx = getInferenceContext4Ast().getContextOfExpression(funcExpr);
    // Reset the maps if required, as the information stored in the maps
    // is dependent on the information given to the CallExpression,
    // thus, different results can be calculated.
    // Reset to assure correct future calculations.
    if (!funcCtx.getInferenceResults().stream().allMatch(infRes ->
        infRes.getLastInferenceMode() == InferenceVisitorMode.TYPE_CHECKING
    )) {
      getType4Ast().reset(funcExpr);
    }
    else {
      getInferenceContext4Ast().reset(funcExpr);
    }
  }

  @Override
  protected void handleFieldAccessResolvedType(
      ASTFieldAccessExpression expr,
      SymTypeExpression resolvedType
  ) {
    CompileTimeTypeCalculator.handleResolvedType(
        expr, resolvedType,
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

}
