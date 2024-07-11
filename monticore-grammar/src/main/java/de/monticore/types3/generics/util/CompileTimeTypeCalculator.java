package de.monticore.types3.generics.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.generics.context.InferenceResult;
import de.monticore.types3.generics.context.InferenceVisitorMode;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.CaptureBound;
import de.monticore.types3.generics.bounds.SubTypingBound;
import de.monticore.types3.generics.constraints.BoundWrapperConstraint;
import de.monticore.types3.generics.constraints.Constraint;
import de.monticore.types3.generics.constraints.ExpressionCompatibilityConstraint;
import de.monticore.types3.generics.constraints.TypeCompatibilityConstraint;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.SymTypeExpressionComparator;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompileTimeTypeCalculator {

  protected static final String LOG_NAME = "CompileTimeTypeCalculator";

  // static delegate

  protected static CompileTimeTypeCalculator delegate;

  public static void init() {
    Log.trace("init default FunctionRelations", "TypeCheck setup");
    CompileTimeTypeCalculator.delegate = new CompileTimeTypeCalculator();
  }

  protected static CompileTimeTypeCalculator getDelegate() {
    if (delegate == null) {
      init();
    }
    return delegate;
  }

  /**
   * Takes a resolved type (or similar) and tries to infer an instantiation.
   * Fills type4Ast and infCtx4Ast with the calculated information.
   * <p>
   * Given, e.g., an FieldAccessExpression or NameExpression,
   * if multiple possible functions / variables are found
   * and the target type is a function type,
   * filter the results and replace the free type variables.
   * E.g.:
   * unfiltered type: (T -> byte) & int
   * target type: short -> short
   * result: short -> byte
   *
   * @param resolvedType empty if nothing was resolved.
   *                     An error should be logged beforehand.
   *                     Free type variables must have been replaced
   *                     with inference variables before calling this method.
   */
  public static void handleResolvedType(
      ASTExpression expr,
      Optional<SymTypeExpression> resolvedType,
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast infCtx4Ast
  ) {
    getDelegate().calculateHandleResolvedType(
        expr, resolvedType, typeTraverser, type4Ast, infCtx4Ast
    );
  }

  protected void calculateHandleResolvedType(
      ASTExpression expr,
      Optional<SymTypeExpression> resolvedType,
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast infCtx4Ast
  ) {
    if (resolvedType.isEmpty()) {
      type4Ast.setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
      return;
    }

    InferenceContext ctx = infCtx4Ast.getContextOfExpression(expr);
    InferenceResult infResult = inferCalledFunction(
        resolvedType.get(), ctx,
        typeTraverser, type4Ast, infCtx4Ast
    );

    if (ctx.getVisitorMode() == InferenceVisitorMode.TYPE_CHECKING ||
        infResult.getLastInferenceMode() == InferenceVisitorMode.TYPE_CHECKING
    ) {
      SymTypeExpression result;
      if (infResult.hasErrorOccurred()) {
        result = SymTypeExpressionFactory.createObscureType();
      }
      else if (infResult.hasResolvedFunction()) {
        // Guaranteed to succeed,
        // else, there would have been an error already.
        result = infResult.getInvocationType().get();
      }
      else {
        // expected to be set
        result = infResult.getResolvedNonInvocationType();
      }
      type4Ast.setTypeOfExpression(expr, result);
    }
    else {
      ctx.setInferredTypes(List.of(infResult));
    }
  }

  protected InferenceResult inferCalledFunction(
      SymTypeExpression resolvedType,
      // has arguments (as expr or types)
      // optional: return target type
      InferenceContext inferenceContext,
      // used to evaluate expressions:
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast inferenceContext4Ast
  ) {
    PartialFunctionInfo funcInfo = inferenceContext.getPartialFunctionInfo();
    replaceExprsWithTypesIffNoTargetTypeRequired(funcInfo,
        typeTraverser, type4Ast, inferenceContext4Ast
    );
    if (!funcInfo.hasParameterCount()) {
      return getResultIfNoFunctionInfoAvailable(resolvedType, inferenceContext);
    }
    InferenceVisitorMode mode = inferenceContext.getVisitorMode();

    // we expect a function, thus get only the functions (filter out vars)
    List<SymTypeOfFunction> resolvedFuncs =
        getFunctionsOfResolvedType(resolvedType);
    List<SymTypeOfFunction> fixArityFuncs =
        fixArities(resolvedFuncs, funcInfo.getParameterCount());
    List<SymTypeOfFunction> potentiallyApplicableFuncs = new ArrayList<>();
    for (SymTypeOfFunction func : fixArityFuncs) {
      if (FunctionRelations.internal_canPotentiallyBeCalledWith(func, funcInfo)) {
        potentiallyApplicableFuncs.add(func);
      }
    }

    Map<SymTypeOfFunction, InferenceResult> func2InferenceResult =
        new TreeMap<>(new SymTypeExpressionComparator());
    for (SymTypeOfFunction func : potentiallyApplicableFuncs) {
      InferenceResult result = new InferenceResult();
      result.setResolvedFunction(func);
      fillTypeParameterBounds(result);
      fillApplicabilityBounds(result, funcInfo);
      if (result.hasErrorOccurred()) {
        return result;
      }
      else if (result.getApplicabilityInstantiation().isPresent()) {
        func2InferenceResult.put(func, result);
      }
    }
    if (func2InferenceResult.isEmpty()) {
      StringBuilder argInfo = new StringBuilder();
      for (int i = 0; i < funcInfo.getParameterCount(); i++) {
        if (funcInfo.hasArgumentType(i)) {
          argInfo
              .append(System.lineSeparator())
              .append(i)
              .append(": ")
              .append(funcInfo.getArgumentType(i).printFullName());
        }
      }
      Log.error("0xFD444 no applicable function found!"
          + System.lineSeparator() + "Arguments pertinent to applicability:"
          + argInfo
          + System.lineSeparator() + " resolved functions:"
          + System.lineSeparator() + resolvedFuncs.stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(System.lineSeparator()))
      );
      InferenceResult result = new InferenceResult();
      result.setHasErrorOccurred();
      return result;
    }

    Optional<SymTypeOfFunction> mostSpecificFunctionOpt =
        FunctionRelations.getMostSpecificFunction(func2InferenceResult.keySet());
    if (mostSpecificFunctionOpt.isEmpty()) {
      Log.error("0xFD446 unable to select a most specific function."
          + System.lineSeparator() + " Applicable functions:"
          + System.lineSeparator() + func2InferenceResult.keySet().stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(System.lineSeparator()))
      );
      InferenceResult result = new InferenceResult();
      result.setHasErrorOccurred();
      return result;
    }

    InferenceResult infResult =
        func2InferenceResult.get(mostSpecificFunctionOpt.get());
    infResult.setLastInferenceMode(InferenceVisitorMode.APPLICABILITY_TEST);

    if (mode == InferenceVisitorMode.APPLICABILITY_TEST) {
      SymTypeOfFunction mostSpecificFunc =
          infResult.getResolvedFunction();
      if (!TypeParameterRelations.hasInferenceVariables(
          mostSpecificFunc.getType()
      )) {
        // enough information to get the type of the expr
        // without relying on target type -> pertinent to applicability
        // -> calculate the type
        mode = InferenceVisitorMode.TYPE_CHECKING;
      }
    }
    if (mode == InferenceVisitorMode.APPLICABILITY_TEST) {
      return infResult;
    }

    // Now a most specific function (compile-time declaration)
    // has been selected
    // Thus, the return type can be added to the constraints
    fillReturnTypeBounds(infResult, funcInfo);
    if (infResult.hasErrorOccurred()) {
      return infResult;
    }
    if (infResult.getInvocationCompatibilityInstantiation().isEmpty()) {
      Log.error("0xFD451 the return type" +
          " of the selected compile-time declaration " +
          infResult.getResolvedFunction().printFullName()
          + "( partially instantiated to "
          + infResult.getApplicabilityInstantiation() + ")"
          + " is not compatible with the target type "
          // There is a return target type, or else there would
          // have already been an error earlier:
          + funcInfo.getReturnTargetType().printFullName()
          + ". Bounds:" + System.lineSeparator()
          + printBounds(infResult.getB3())
      );
      infResult.setHasErrorOccurred();
      return infResult;
    }

    fillInvocationTypeConstraints(
        infResult, funcInfo, typeTraverser, type4Ast, inferenceContext4Ast
    );
    if (infResult.hasErrorOccurred()) {
      return infResult;
    }
    infResult.setLastInferenceMode(InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION);
    if (mode == InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION) {
      return infResult;
    }

    // mode is type checking ->
    // expect results / target type without inference variables
    if (inferenceContext.hasTargetType() && TypeParameterRelations
        .hasInferenceVariables(inferenceContext.getTargetType())
    ) {
      Log.error("0xFD452 internal error: "
          + "entered final phase of calculating an invocation type for "
          + mostSpecificFunctionOpt.get().printFullName()
          + ", during this, expected a target type without free variables"
          + ", but got " + inferenceContext.getTargetType().printFullName()
      );
      infResult.setHasErrorOccurred();
      return infResult;
    }

    fillInvocationTypeBounds(
        infResult, typeTraverser, type4Ast, inferenceContext4Ast
    );
    if (infResult.hasErrorOccurred()) {
      return infResult;
    }
    infResult.setLastInferenceMode(InferenceVisitorMode.TYPE_CHECKING);
    if (infResult.getInvocationType().isEmpty()) {
      Log.error("0xFD447 cannot resolve function invocation type"
          + " for compile-time declaration " +
          infResult.getResolvedFunction().printFullName()
          + (funcInfo.hasReturnTargetType()
          ? " with the target type "
          + funcInfo.getReturnTargetType().printFullName()
          : "")
          + ". Bounds:" + System.lineSeparator()
          + printBounds(infResult.getB4())
      );
      infResult.setHasErrorOccurred();
      return infResult;
    }

    return infResult;
  }

  /**
   * In the PartialFunctionInfo,
   * given argument-expressions, these are replaced with types,
   * iff a type can be calculated without access to a target type.
   * E.g., "2+3" is replaced with int, but "new Set<>()" is not replaced,
   * as the type is not fully known without a target type.
   */
  protected void replaceExprsWithTypesIffNoTargetTypeRequired(
      PartialFunctionInfo funcInfo,
      // used to evaluate expressions
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast inferenceContext4Ast
  ) {
    if (!funcInfo.hasParameterCount()) {
      return;
    }
    for (int i = 0; i < funcInfo.getParameterCount(); i++) {
      if (!funcInfo.hasArgumentType(i) && funcInfo.hasArgumentExpr(i)) {
        ASTExpression expr = funcInfo.getArgumentExpr(i);
        type4Ast.reset(expr);
        inferenceContext4Ast.reset(expr);
        InferenceContext infCtx = inferenceContext4Ast.getContextOfExpression(expr);
        infCtx.setVisitorMode(InferenceVisitorMode.APPLICABILITY_TEST);
        expr.accept(typeTraverser);
        infCtx = inferenceContext4Ast.getContextOfExpression(expr);
        if (infCtx.getInferenceResults().size() >= 1) {
          boolean hasResults = true;
          List<SymTypeExpression> instantiations = new ArrayList<>();
          for (InferenceResult infResult : infCtx.getInferenceResults()) {
            Optional<SymTypeExpression> compileTimeType = Optional.empty();
            if (infResult.getLastInferenceMode() == InferenceVisitorMode.TYPE_CHECKING) {
              compileTimeType = infResult.getCompileTimeType();
            }
            if (compileTimeType.isEmpty()) {
              hasResults = false;
            }
            else {
              instantiations.add(compileTimeType.get());
            }
          }
          if (hasResults) {
            SymTypeExpression argType =
                SymTypeExpressionFactory.createUnionOrDefault(
                    SymTypeExpressionFactory.createObscureType(),
                    instantiations
                );
            SymTypeExpression capArgType = TypeParameterRelations.getCaptureConverted(argType);
            // this part may(!) need to be extended
            // if the corresponding case becomes relevant.
            if (!TypeParameterRelations.hasInferenceVariables(capArgType)) {
              funcInfo.setArgumentType(i, capArgType);
            }
          }
        }
        else if (type4Ast.hasPartialTypeOfExpression(expr)) {
          SymTypeExpression argType = type4Ast.getPartialTypeOfExpr(expr);
          funcInfo.setArgumentType(i, argType);
        }
        inferenceContext4Ast.reset(expr);
        type4Ast.reset(expr);
      }
    }
  }

  /**
   * Returns the result for the cases that
   * 1. a non-function is expected and as such, non will be inferred
   * 2. not enough information is given to assert that a function is expected
   */
  protected InferenceResult getResultIfNoFunctionInfoAvailable(
      SymTypeExpression resolvedType,
      InferenceContext inferenceContext
  ) {
    InferenceResult result = new InferenceResult();
    PartialFunctionInfo functionInfo = inferenceContext.getPartialFunctionInfo();
    if (functionInfo.hasParameterCount()) {
      Log.error("0xFD284 internal error: inapplicable call to this method.");
      result.setHasErrorOccurred();
    }
    else if (inferenceContext.hasTargetType()) {
      SymTypeExpression targetType = inferenceContext.getTargetType();
      if (!TypeParameterRelations.hasInferenceVariables(targetType)) {
        result.setLastInferenceMode(InferenceVisitorMode.TYPE_CHECKING);
        // a type is expected which is NOT a function type
        // filter out all function types:
        Optional<SymTypeExpression> inferredType =
            getNonFunctionOfResolvedType(resolvedType);
        if (inferredType.isPresent()) {
          result.setResolvedNonInvocationType(inferredType.get());
        }
        else {
          // no info available
          Log.error("0xFD449 given target type "
              + targetType.printFullName()
              + ", but resolved no compatible type: "
              + resolvedType.printFullName()
          );
          result.setHasErrorOccurred();
        }
      }
      // target type has a type variable
      else {
        switch (inferenceContext.getVisitorMode()) {
          case APPLICABILITY_TEST:
            result.setLastInferenceMode(InferenceVisitorMode.APPLICABILITY_TEST);
            result.setResolvedNonInvocationType(resolvedType);
            break;
          case EXPRESSION_COMPATIBILITY_REDUCTION:
            result.setLastInferenceMode(InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION);
            TypeCompatibilityConstraint constraint =
                new TypeCompatibilityConstraint(resolvedType, targetType);
            result.setB3(ConstraintReduction.reduce((Constraint) constraint));
            result.setB4C(Collections.emptyList());
            result.setResolvedNonInvocationType(resolvedType);
            break;
          case TYPE_CHECKING:
            Log.error("0xFD448 internal error: unable to handle " +
                "resolved type due to lack of information" +
                " (did not expect free type variables): "
                + resolvedType.printFullName()
            );
            result.setHasErrorOccurred();
            break;
        }
      }
    }
    else {
      if (TypeParameterRelations.hasInferenceVariables(resolvedType) &&
          inferenceContext.getVisitorMode() != InferenceVisitorMode.APPLICABILITY_TEST
      ) {
        Log.error("0xFD450 internal error: "
            + "expected a target type to be available"
            + " (type-check misconfigured?)"
            + ", as the resolved type contains free type variables: "
            + resolvedType.printFullName()
        );
        result.setHasErrorOccurred();
      }
      // This part is not quite correct;
      // This case should check if multiple types have been resolved
      // in the case that only one has been resolved,
      // and it is an intersection type, this case should not apply.
      // For this, intersections would need to be marked
      // or no intersections should be returned in the first place.
      else if (SymTypeRelations.normalize(resolvedType).isIntersectionType()
          || TypeParameterRelations.hasInferenceVariables(resolvedType)
      ) {
        result.setLastInferenceMode(InferenceVisitorMode.APPLICABILITY_TEST);
        result.setResolvedNonInvocationType(resolvedType);
      }
      else {
        result.setLastInferenceMode(InferenceVisitorMode.TYPE_CHECKING);
        result.setResolvedNonInvocationType(resolvedType);
      }
    }
    return result;
  }

  /**
   * Fixes arity to the specified value.
   * Filters out all functions that cannot have the specified arity.
   */
  protected List<SymTypeOfFunction> fixArities(
      List<SymTypeOfFunction> functions,
      int parameterCount
  ) {
    List<SymTypeOfFunction> funcsFixedArity = functions.stream()
        .filter(f -> f.canHaveArity(parameterCount))
        .map(f -> f.getWithFixedArity(parameterCount))
        .collect(Collectors.toList());
    return funcsFixedArity;
  }

  protected void fillTypeParameterBounds(
      InferenceResult inferenceResult
  ) {
    List<Bound> bounds = new ArrayList<>();
    SymTypeOfFunction func = inferenceResult.getResolvedFunction();
    Map<SymTypeVariable, SymTypeVariable> typeParamReplaceMap =
        getParamReplaceMap(func);
    for (Map.Entry<SymTypeVariable, SymTypeVariable> param2InfVar :
        typeParamReplaceMap.entrySet()
    ) {
      SymTypeVariable typeVar = param2InfVar.getValue();
      SymTypeVariable parameter = param2InfVar.getKey();
      SymTypeExpression upperBound = parameter.getUpperBound();
      SymTypeExpression upperBoundWithInfVars = TypeParameterRelations
          .replaceTypeVariables(upperBound, typeParamReplaceMap);
      bounds.add(new SubTypingBound(typeVar, upperBoundWithInfVars));
    }
    // can always calculate B0 (assuming no internal error)
    inferenceResult.setB0(bounds);
  }

  /**
   * {@link #replaceExprsWithTypesIffNoTargetTypeRequired(PartialFunctionInfo, ITraverser, Type4Ast, InferenceContext4Ast)}
   * has to be called before this function.
   */
  protected void fillApplicabilityBounds(
      InferenceResult inferenceResult,
      PartialFunctionInfo funcInfo
  ) {
    List<Bound> bounds = new ArrayList<>(inferenceResult.getB0());
    SymTypeOfFunction func = inferenceResult.getResolvedFunction();
    // collect constraints
    List<Constraint> constraints = new ArrayList<>();
    // note: do not use return type for applicability testing,
    // only parameters
    for (int i = 0; i < funcInfo.getParameterCount(); i++) {
      int parIdx = Math.min(func.sizeArgumentTypes(), i);
      if (funcInfo.hasArgumentType(i)) {
        SymTypeExpression argType = funcInfo.getArgumentType(i);
        SymTypeExpression argTypeCap;
        if (TypeParameterRelations.hasWildcards(argType)) {
          argTypeCap = TypeParameterRelations.getCaptureConverted(argType);
        }
        else {
          argTypeCap = argType;
        }
        constraints.add(new TypeCompatibilityConstraint(
            argTypeCap,
            func.getArgumentType(parIdx)
        ));
      }
    }
    List<Bound> newBounds = ConstraintReduction.reduce(constraints);
    bounds.addAll(newBounds);
    inferenceResult.setB2(bounds);
  }

  protected void fillReturnTypeBounds(
      InferenceResult inferenceResult,
      PartialFunctionInfo funcInfo
  ) {
    List<Bound> bounds = new ArrayList<>(inferenceResult.getB2());
    SymTypeOfFunction func = inferenceResult.getResolvedFunction();
    // add return type constraints
    // to resolve the actual type of the function
    // s. a. JLS 21 18.5.2
    // support calls to generic methods without a given target type,
    // e.g., id(1) here, id will have type int -> int
    if (funcInfo.hasReturnTargetType()) {
      List<Constraint> newConstraints = new ArrayList<>();
      SymTypeExpression capReturnType;
      // This check could be optimized further (s. Java Spec 21 18.5.2.1)
      SymTypeExpression returnType = func.getType();
      if (TypeParameterRelations.hasWildcards(returnType)) {
        if (TypeParameterRelations.hasInferenceVariables(returnType)) {
          CaptureBound captureBound = new CaptureBound(returnType);
          newConstraints.add(new BoundWrapperConstraint(captureBound));
          capReturnType = captureBound.getPlaceHolder();
        }
        else {
          capReturnType = TypeParameterRelations.getCaptureConverted(returnType);
        }
      }
      else {
        capReturnType = returnType;
      }
      Constraint returnTypeConstraint = new TypeCompatibilityConstraint(
          capReturnType, funcInfo.getReturnTargetType()
      );
      newConstraints.add(returnTypeConstraint);
      List<Bound> newBounds = ConstraintReduction.reduce(newConstraints);
      bounds.addAll(newBounds);
    }
    inferenceResult.setB3(bounds);
  }

  protected void fillInvocationTypeConstraints(
      InferenceResult inferenceResult,
      PartialFunctionInfo funcInfo,
      // used to evaluate expressions
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast inferenceContext4Ast
  ) {
    List<ExpressionCompatibilityConstraint> constraints = new ArrayList<>();
    SymTypeOfFunction func = inferenceResult.getResolvedFunction();
    for (int i = 0; i < funcInfo.getParameterCount(); i++) {
      int parIdx = Math.min(func.sizeArgumentTypes(), i);
      if (!funcInfo.hasArgumentType(i)) {
        // not pertinent to applicability, thus here
        ASTExpression argExpr = funcInfo.getArgumentExpr(i);
        SymTypeExpression paramType = func.getArgumentType(parIdx);
        constraints.add(new ExpressionCompatibilityConstraint(
            argExpr, paramType
        ));
        // collect constraints from within the expression as well
        // at this point, there is only one applicable function,
        // thus, where is no need to reset the inference map anymore
        InferenceContext argCtx = inferenceContext4Ast
            .getContextOfExpression(argExpr);
        argCtx.setTargetType(paramType);
        argCtx.setVisitorMode(
            InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION
        );
        argExpr.accept(typeTraverser);
        argCtx = inferenceContext4Ast
            .getContextOfExpression(argExpr);
        if (argCtx.getInferenceResults().isEmpty()) {
          Log.error("0xFD140 internal error: expected inference results",
              argExpr.get_SourcePositionStart(),
              argExpr.get_SourcePositionEnd()
          );
          inferenceResult.setHasErrorOccurred();
        }
        for (InferenceResult argResult : argCtx.getInferenceResults()) {
          if (argResult.hasErrorOccurred()) {
            inferenceResult.setHasErrorOccurred();
          }
          else {
            constraints.addAll(argResult.getB4C());
          }
        }
      }
    }
    inferenceResult.setB4C(constraints);
  }

  protected void fillInvocationTypeBounds(
      InferenceResult inferenceResult,
      // used to evaluate expressions
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast inferenceContext4Ast
  ) {
    List<Bound> bounds = new ArrayList<>(inferenceResult.getB3());
    List<ExpressionCompatibilityConstraint> constraints =
        inferenceResult.getB4C();
    // this is a simplification of JLS 21 18.5.2.2
    // this may need to be extended with constraint ordering
    List<Bound> newBounds = new ArrayList<>();
    for (ExpressionCompatibilityConstraint constraint : constraints) {
      Optional<List<Bound>> constraintBounds =
          reduceExpressionCompatibilityConstraint(
              constraint, typeTraverser, type4Ast, inferenceContext4Ast
          );
      if (constraintBounds.isEmpty()) {
        inferenceResult.setHasErrorOccurred();
      }
      else {
        newBounds.addAll(constraintBounds.get());
      }
    }
    bounds.addAll(newBounds);
    inferenceResult.setB4(bounds);
  }

  /**
   * Reduces constraints of the form <Expr --> type>.
   * WARNING: While <Expr --> type> IS a constraint,
   * it is only ever used in this class;
   * This is used OUTSIDE of
   * {@link de.monticore.types3.generics.util.ConstraintReduction},
   * as a way to describe a {@link TypeCompatibilityConstraint},
   * without having a type available.
   * S. a. Java Spec 21 18.1.2
   *
   * @return empty on error, reduced bounds otherwise
   */
  protected Optional<List<Bound>> reduceExpressionCompatibilityConstraint(
      ExpressionCompatibilityConstraint constraint,
      // used to evaluate the expression
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      InferenceContext4Ast inferenceContext4Ast
  ) {
    List<Bound> bounds = new ArrayList<>();
    ASTExpression sourceExpr = constraint.getExpr();
    // may not need to reset here, but kept for consistency
    type4Ast.reset(sourceExpr);
    inferenceContext4Ast.reset(sourceExpr);
    InferenceContext sourceCtx = inferenceContext4Ast
        .getContextOfExpression(sourceExpr);
    sourceCtx.setVisitorMode(
        InferenceVisitorMode.EXPRESSION_COMPATIBILITY_REDUCTION
    );
    sourceCtx.setTargetType(constraint.getTargetType());
    sourceExpr.accept(typeTraverser);
    sourceCtx = inferenceContext4Ast.getContextOfExpression(sourceExpr);
    type4Ast.reset(sourceExpr);
    inferenceContext4Ast.reset(sourceExpr);
    if (sourceCtx.getInferenceResults().isEmpty()) {
      Log.error("0xFD77D internal error: "
              + "Got no information returned for reduction of "
              + constraint.print()
              + ", which may indicate an incorrect type-checker setup?",
          sourceExpr.get_SourcePositionStart(),
          sourceExpr.get_SourcePositionEnd()
      );
      return Optional.empty();
    }
    for (InferenceResult argResult : sourceCtx.getInferenceResults()) {
      if (argResult.hasErrorOccurred()) {
        return Optional.empty();
      }
      else {
        bounds.addAll(argResult.getB3());
      }
    }
    return Optional.of(bounds);
  }

  // Helper

  protected List<SymTypeOfFunction> getFunctionsOfResolvedType(
      SymTypeExpression resolvedType
  ) {
    List<SymTypeOfFunction> resolvedFuncs;
    if (resolvedType.isIntersectionType()) {
      resolvedFuncs =
          resolvedType.asIntersectionType().getIntersectedTypeSet().stream()
              .filter(SymTypeExpression::isFunctionType)
              .map(SymTypeExpression::asFunctionType)
              .collect(Collectors.toList());
    }
    else if (resolvedType.isFunctionType()) {
      resolvedFuncs = Collections.singletonList(resolvedType.asFunctionType());
    }
    else {
      resolvedFuncs = Collections.emptyList();
    }
    return resolvedFuncs;
  }

  protected Optional<SymTypeExpression> getNonFunctionOfResolvedType(
      SymTypeExpression resolvedType
  ) {
    Optional<SymTypeExpression> nonFunctionType;
    List<SymTypeExpression> resolvedTypes;
    if (resolvedType.isIntersectionType()) {
      resolvedTypes = new ArrayList<>(
          resolvedType.asIntersectionType().getIntersectedTypeSet()
      );
    }
    else {
      resolvedTypes = Collections.singletonList(resolvedType);
    }
    List<SymTypeExpression> nonFuncs = resolvedTypes.stream()
        .filter(Predicate.not(SymTypeExpression::isFunctionType))
        .collect(Collectors.toList());
    // more than 1 non-function type is in most languages not expected
    // if there are no non-functions, return the function(s),
    // this gets filtered outside of inference
    if (nonFuncs.size() >= 1) {
      nonFunctionType = Optional.of(SymTypeExpressionFactory
          .createIntersectionOrDefault(
              resolvedType, nonFuncs
          )
      );
    }
    else {
      nonFunctionType = Optional.empty();
    }
    return nonFunctionType;
  }

  /**
   * given a resolved function (with inference variables),
   * returns a map that replaces the type parameters of the declared function
   * with the inference variables.
   * In JLS 21 18.1.3: [P1:=α1,...,Pn:=αn]
   */
  protected Map<SymTypeVariable, SymTypeVariable> getParamReplaceMap(
      SymTypeOfFunction func
  ) {
    List<SymTypeVariable> typeVars = func.getTypeArguments().stream()
        .map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    SymTypeOfFunction declaredFunc = func.getDeclaredType();
    List<SymTypeVariable> typeParams = declaredFunc.getTypeArguments().stream()
        .map(SymTypeExpression::asTypeVariable)
        .collect(Collectors.toList());
    Map<SymTypeVariable, SymTypeVariable> typeParamReplaceMap =
        new TreeMap<>(new SymTypeExpressionComparator());
    for (int i = 0; i < typeParams.size(); i++) {
      typeParamReplaceMap.put(typeParams.get(i), typeVars.get(i));
    }
    for (int i = 0; i < typeVars.size(); i++) {
      SymTypeVariable typeVar = typeVars.get(i);
      SymTypeVariable parameter = typeParams.get(i);
      // small check asserting correct input
      if (!TypeParameterRelations.isInferenceVariable(typeVar) ||
          TypeParameterRelations.isInferenceVariable(parameter) ||
          !SymTypeRelations.isBottom(parameter.getLowerBound())) {
        Log.error("0xFD147 internal error: unexpected input to fill B0");
      }
    }
    return typeParamReplaceMap;
  }

  protected String printBounds(List<Bound> bounds) {
    return bounds.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }

}
