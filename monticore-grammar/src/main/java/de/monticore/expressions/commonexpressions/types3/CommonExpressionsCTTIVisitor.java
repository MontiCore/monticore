/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.generics.constraints.ExpressionCompatibilityConstraint;
import de.monticore.types3.generics.context.InferenceContext;
import de.monticore.types3.generics.context.InferenceResult;
import de.monticore.types3.generics.context.InferenceVisitorMode;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

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
    if (getInferenceContext4Ast().getContextOfExpression(expr)
        .getVisitorMode() != InferenceVisitorMode.TYPE_CHECKING
    ) {
      expr.getTrueExpression().accept(getTraverser());
      expr.getFalseExpression().accept(getTraverser());
      List<InferenceResult> infRess = new ArrayList<>();
      infRess.addAll(getInferenceContext4Ast()
          .getContextOfExpression(expr.getTrueExpression())
          .getInferenceResults()
      );
      infRess.addAll(getInferenceContext4Ast()
          .getContextOfExpression(expr.getFalseExpression())
          .getInferenceResults()
      );
      getInferenceContext4Ast().getContextOfExpression(expr)
          .setInferredTypes(infRess);
    }
    else {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

  @Override
  public void handle(ASTBracketExpression expr) {
    InferenceContext infCtx = getInferenceContext4Ast().getContextOfExpression(expr);
    getInferenceContext4Ast().setContextOfExpression(
        expr.getExpression(), infCtx
    );

    if (infCtx.getVisitorMode() != InferenceVisitorMode.TYPE_CHECKING) {
      expr.getExpression().accept(getTraverser());
    }
    else {
      visit(expr);
      traverse(expr);
      endVisit(expr);
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

    InferenceContext funcCtx = visitForInference(expr);

    List<InferenceResult> inferenceResults = funcCtx.getInferenceResults();
    // small check that the visitor is configured correctly
    if (mode == InferenceVisitorMode.TYPE_CHECKING &&
        !inferenceResults.isEmpty()
    ) {
      Log.error("0xFD212 internal error: unexpected inference result."
              + " TypeCheck misconfigured?",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      getType4Ast().setTypeOfExpression(expr, createObscureType());
      return;
    }
    // handle errors
    if (inferenceResults.stream().anyMatch(InferenceResult::hasErrorOccurred)
        || (getType4Ast().hasPartialTypeOfExpression(expr.getExpression())
        && getType4Ast().getPartialTypeOfExpr(expr.getExpression()).isObscureType())
    ) {
      // already logged
      getType4Ast().setTypeOfExpression(expr, createObscureType());
      return;
    }

    //pass inference information upwards
    if (!inferenceResults.isEmpty()) {
      if (inferenceResults.stream().anyMatch(
          infRes -> infRes.getLastInferenceMode()
              == InferenceVisitorMode.APPLICABILITY_TEST
      )) {
        // Since there are inference results,
        // there is a function with a return type that must be inferred.
        // Thus, this callExpression is not pertinent to applicability
        InferenceResult applicabilityRes = new InferenceResult();
        applicabilityRes.setLastInferenceMode(InferenceVisitorMode.APPLICABILITY_TEST);
        callCtx.setInferredTypes(List.of(applicabilityRes));
      }
      else if (inferenceResults.stream().allMatch(infRes -> infRes.getLastInferenceMode()
          == InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION
      )) {
        List<ExpressionCompatibilityConstraint> constraints = new ArrayList<>();
        for (InferenceResult inferenceResult : inferenceResults) {
          constraints.addAll(inferenceResult.getB4C());
        }
        InferenceResult applicabilityRes = new InferenceResult();
        applicabilityRes.setLastInferenceMode(InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION);
        applicabilityRes.setB3(Collections.emptyList());
        applicabilityRes.setB4C(constraints);
        callCtx.setInferredTypes(List.of(applicabilityRes));
      }
    }
    // or calculate the type
    else {
      // here: mode = InferenceVisitorMode.TYPE_CHECKING;
      // at this point, the function type has been calculated

      // set argument target types
      // the calculated function type should
      // 1. not contain any inference variables
      // 2. and have fixed arity
      SymTypeOfFunction targetFunction = getType4Ast()
          .getPartialTypeOfExpr(expr.getExpression()).asFunctionType();
      for (int i = 0; i < expr.getArguments().sizeExpressions(); i++) {
        ASTExpression argExpr = expr.getArguments().getExpression(i);
        getInferenceContext4Ast().setTargetTypeOfExpression(
            argExpr,
            targetFunction.getArgumentType(i)
        );
      }

      visit(expr);
      // traverse without already calculated function
      expr.getArguments().accept(getTraverser());
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
  protected InferenceContext visitForInference(ASTCallExpression expr) {
    InferenceContext callCtx = getInferenceContext4Ast().getContextOfExpression(expr);
    InferenceVisitorMode mode = callCtx.getVisitorMode();
    ASTExpression funcExpr = expr.getExpression();
    InferenceContext funcCtx = new InferenceContext();
    funcCtx.setVisitorMode(mode);
    int parCount = expr.getArguments().sizeExpressions();
    funcCtx.getPartialFunctionInfo().setParameterCount(parCount);
    for (int i = 0; i < parCount; i++) {
      funcCtx.getPartialFunctionInfo().setArgumentExpr(i, expr.getArguments().getExpression(i));
    }
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
    getInferenceContext4Ast().reset(funcExpr);
    return funcCtx;
  }

  @Override
  protected void handleFieldAccessResolvedType(
      ASTFieldAccessExpression expr,
      SymTypeExpression resolvedType
  ) {
    CompileTimeTypeCalculator.handleResolvedType(
        expr, Optional.of(resolvedType),
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

}
