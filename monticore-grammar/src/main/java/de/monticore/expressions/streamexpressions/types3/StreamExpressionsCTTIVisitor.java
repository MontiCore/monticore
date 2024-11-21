package de.monticore.expressions.streamexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConcatStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsHandler;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;
import de.monticore.types3.streams.StreamSymTypeFactory;
import de.monticore.types3.streams.StreamSymTypeRelations;

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
    // default Timing is Event -> never create a Stream without Timing
    // replace any Stream with EventStream after inference is done,
    // as default types are not a feature the inference algorithm can handle,
    // or needs to handle -> just override the result.
    // todo FDr test
    if (getType4Ast().hasTypeOfExpression(expr)) {
      SymTypeOfGenerics streamType = getType4Ast()
          .getTypeOfExpression(expr).asGenericType();
      if (StreamSymTypeRelations.isStreamOfUnknownSubType(streamType)) {
        SymTypeOfGenerics eventStreamType =
            StreamSymTypeFactory.createEventStream(
                StreamSymTypeRelations.getStreamElementType(streamType)
            );
        getType4Ast().setTypeOfExpression(expr, eventStreamType);
      }
    }
    if (getType4Ast().hasPartialTypeOfExpression(expr) &&
        !getType4Ast().getPartialTypeOfExpr(expr).isObscureType()
    ) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
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
   * {@code <T> (T...) -> StreamType<T>} for subtypes of Stream or
   * {@code <T, S extends Stream<T>> (T...) -> S} for Stream
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
    else if (expr.isUntimed()) {
      resultType = StreamSymTypeFactory.createUntimedStream(typeVarT);
    }
    else {
      resultType = createTypeVariable(
          createBottomType(),
          StreamSymTypeFactory.createStream(typeVarT)
      );
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
