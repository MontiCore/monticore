/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.streamexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendAbsentStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendTickStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConcatStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsHandler;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;
import de.monticore.types3.streams.StreamSymTypeFactory;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createTopType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;

public class StreamExpressionsCTTIVisitor extends StreamExpressionsTypeVisitor
    implements StreamExpressionsHandler {

  protected StreamExpressionsTraverser traverser;

  @Override
  public StreamExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(StreamExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTStreamConstructorExpression expr) {
    handleCTTIFuncCall(expr);
    if (getType4Ast().hasPartialTypeOfExpression(expr) &&
        !getType4Ast().getPartialTypeOfExpr(expr).isObscureType()
    ) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

  protected void handleCTTIFuncCall(ASTStreamConstructorExpression expr) {
    SymTypeOfFunction exprFunc;
    List<ASTExpression> containedExprs = expr.getExpressionList();
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getStreamConstructorFunc(expr);
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc.getWithFixedArity(containedExprs.size()),
        containedExprs,
        getTraverser(), getType4Ast(), getInferenceContext4Ast()
    );
  }

  @Override
  public void handle(ASTAppendStreamExpression expr) {
    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getAppendStreamFunc();
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc,
        List.of(expr.getLeft(), expr.getRight()),
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

  @Override
  public void handle(ASTAppendAbsentStreamExpression expr) {
    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getAppendAbsentStreamFunc();
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc,
        List.of(expr.getStream()),
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

  @Override
  public void handle(ASTAppendTickStreamExpression expr) {
    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getAppendTickStreamFunc();
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc,
        List.of(expr.getStream()),
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

  @Override
  public void handle(ASTConcatStreamExpression expr) {
    SymTypeOfFunction exprFunc;
    if (getInferenceContext4Ast().hasResolvedOfExpression(expr)) {
      exprFunc = getInferenceContext4Ast().getResolvedOfExpression(expr)
          .asFunctionType();
    }
    else {
      exprFunc = getStreamConcatFunc();
      getInferenceContext4Ast().setResolvedOfExpression(expr, exprFunc);
    }
    CompileTimeTypeCalculator.handleCall(
        expr,
        exprFunc,
        List.of(expr.getLeft(), expr.getRight()),
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

  /**
   * {@code <T> (T...) -> StreamType<T>}
   * for the subtype of Stream corresponding to the constructor
   */
  protected SymTypeOfFunction getStreamConstructorFunc(
      ASTStreamConstructorExpression expr
  ) {
    SymTypeVariable typeVarT = createTypeVariable(
        createBottomType(),
        createTopType()
    );
    SymTypeExpression resultType;
    if (expr.isEventTimed()) {
      resultType = StreamSymTypeFactory.createEventStream(typeVarT);
    }
    else if (expr.isSyncTimed()) {
      resultType = StreamSymTypeFactory.createSyncStream(typeVarT);
    }
    else if (expr.isToptTimed()) {
      resultType = StreamSymTypeFactory.createToptStream(typeVarT);
    }
    else {
      resultType = StreamSymTypeFactory.createUntimedStream(typeVarT);
    }
    SymTypeOfFunction streamConstructorFunc =
        createFunction(resultType, List.of(typeVarT), true);
    return streamConstructorFunc;
  }

  /**
   * {@code <T, S extends Stream<T>> (T, S) -> S}
   */
  protected SymTypeOfFunction getAppendStreamFunc() {
    SymTypeVariable typeVarT = createTypeVariable(
        createBottomType(),
        createTopType()
    );
    SymTypeVariable typeVarS = createTypeVariable(
        createBottomType(),
        StreamSymTypeFactory.createStream(typeVarT)
    );
    SymTypeOfFunction appendStreamFunc = createFunction(
        typeVarS, List.of(typeVarT, typeVarS)
    );
    return appendStreamFunc;
  }

  /**
   * {@code <T> ToptStream<T> -> ToptStream<T>}
   */
  protected SymTypeOfFunction getAppendAbsentStreamFunc() {
    SymTypeVariable typeVarT = createTypeVariable(
        createBottomType(),
        createTopType()
    );
    SymTypeExpression argType =
        StreamSymTypeFactory.createToptStream(typeVarT);
    SymTypeOfFunction appendAbsentStreamFunc =
        createFunction(argType, List.of(argType));
    return appendAbsentStreamFunc;
  }

  /**
   * {@code <T> EventStream<T> -> EventStream<T>}
   */
  protected SymTypeOfFunction getAppendTickStreamFunc() {
    SymTypeVariable typeVarT = createTypeVariable(
        createBottomType(),
        createTopType()
    );
    SymTypeExpression argType =
        StreamSymTypeFactory.createEventStream(typeVarT);
    SymTypeOfFunction appendTickStreamFunc =
        createFunction(argType, List.of(argType));
    return appendTickStreamFunc;
  }

  /**
   * {@code <T, S extends Stream<T>> (S, S) -> S}
   */
  protected SymTypeOfFunction getStreamConcatFunc() {
    SymTypeVariable typeVarT = createTypeVariable(
        createBottomType(),
        createTopType()
    );
    SymTypeVariable typeVarS = createTypeVariable(
        createBottomType(),
        StreamSymTypeFactory.createStream(typeVarT)
    );
    SymTypeOfFunction streamConcatFunc = createFunction(
        typeVarS, List.of(typeVarS, typeVarS)
    );
    return streamConcatFunc;
  }

}
