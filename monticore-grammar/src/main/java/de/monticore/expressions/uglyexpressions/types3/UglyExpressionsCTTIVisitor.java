package de.monticore.expressions.uglyexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.uglyexpressions.UglyExpressionsMill;
import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsHandler;
import de.monticore.expressions.uglyexpressions._visitor.UglyExpressionsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.generics.context.InferenceContext;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;
import de.monticore.types3.generics.util.PartialFunctionInfo;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class UglyExpressionsCTTIVisitor
    extends UglyExpressionsTypeVisitor
    implements UglyExpressionsHandler {

  protected UglyExpressionsTraverser traverser;

  @Override
  public UglyExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(UglyExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTTypeCastExpression expr) {
    // type to be casted to is the target for the inner expression
    expr.getMCType().accept(getTraverser());
    SymTypeExpression typeResult = getType4Ast().getPartialTypeOfTypeId(expr.getMCType());
    if (typeResult.isObscureType()) {
      getType4Ast().setTypeOfExpression(expr, createObscureType());
    }
    else {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

  @Override
  public void handle(ASTCreatorExpression expr) {
    InferenceContext infCtx = getInferenceContext4Ast().getContextOfExpression(expr);
    if (UglyExpressionsMill.typeDispatcher()
        .isUglyExpressionsASTClassCreator(expr.getCreator())
    ) {
      handleClassCreatorExpr(expr, infCtx);
    }
    // no custom implementation
    else {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

  protected void handleClassCreatorExpr(
      ASTCreatorExpression expr,
      InferenceContext infCtx
  ) {
    ASTClassCreator creator = UglyExpressionsMill.typeDispatcher()
        .asUglyExpressionsASTClassCreator(expr.getCreator());
    SymTypeExpression resolved;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      resolved = getInferenceContext4Ast().getResolvedOfExpression(expr);
    }
    else {
      if (!getType4Ast().hasPartialTypeOfTypeIdentifier(creator.getMCType())) {
        creator.getMCType().accept(getTraverser());
      }
      List<SymTypeOfFunction> constructors = getConstructors(creator);
      if (constructors.isEmpty()) {
        // error already logged
        getType4Ast().setTypeOfExpression(expr, createObscureType());
        return;
      }
      resolved = SymTypeExpressionFactory.createIntersectionOrDefault(
          createObscureType(), constructors
      );
      getInferenceContext4Ast().setResolvedOfExpression(expr, resolved);
    }

    // a "fake" ASTExpression to store the calculated function type in,
    // as there is only one expr but two calculations;
    // calculating the function type and calculating the type of the call
    ASTExpression tmpFuncExpr = expr.deepClone();
    PartialFunctionInfo funcInfo = getInferenceContext4Ast()
        .getContextOfExpression(tmpFuncExpr).getPartialFunctionInfo();
    if (infCtx.hasTargetType()) {
      funcInfo.setReturnTargetType(infCtx.getTargetType());
    }
    funcInfo.setArgumentExprs(creator.getArguments().getExpressionList());
    CompileTimeTypeCalculator.handleResolvedType(
        tmpFuncExpr, resolved,
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
    CompileTimeTypeCalculator.handleCall(
        expr, tmpFuncExpr, creator.getArguments().getExpressionList(),
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
    // clean the map by removing the temporary ASTExpression
    getType4Ast().reset(tmpFuncExpr);
    getInferenceContext4Ast().resetContexts(tmpFuncExpr);

    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      if (!getType4Ast().getPartialTypeOfExpr(expr).isObscureType()) {
        visit(expr);
        traverse(expr);
        endVisit(expr);
      }
    }
  }

}
