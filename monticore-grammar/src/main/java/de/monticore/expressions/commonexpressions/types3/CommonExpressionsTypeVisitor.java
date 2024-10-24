/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._util.ICommonExpressionsTypeDispatcher;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.NameExpressionTypeCalculator;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

/**
 * This Visitor can calculate a SymTypeExpression (type)
 * for the expressions in CommonExpressions.
 * It can be combined with other expressions in your language
 */
public class CommonExpressionsTypeVisitor extends AbstractTypeVisitor
    implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  // due to legacy reasons, error codes are identical for different operators,
  // even if they use different implementations now,
  // e.g., adding SIUnits required different implementations for '*' and '/'.
  protected static final String ARITHMETIC_OPERATOR_ERROR_CODE = "0xB0163";
  protected static final String NUMERIC_PREFIX_ERROR_CODE = "0xA017D";
  protected static final String EQUALITY_OPERATOR_ERROR_CODE = "0xB0166";
  protected static final String NUMERIC_COMPARISON_ERROR_CODE = "0xB0167";
  protected static final String BOOLEAN_CONDITIONAL_ERROR_CODE = "0xB0113";

  protected CommonExpressionsTraverser traverser;

  protected TypeVisitorOperatorCalculator operatorCalculator;

  protected WithinTypeBasicSymbolsResolver withinTypeResolver;

  protected TypeContextCalculator typeCtxCalc;

  // should be the same as used in DeriveSymTypeOfExpressionBasis
  protected WithinScopeBasicSymbolsResolver withinScopeResolver;

  protected CommonExpressionsTypeVisitor(
      TypeVisitorOperatorCalculator operatorCalculator,
      WithinTypeBasicSymbolsResolver withinTypeResolver,
      TypeContextCalculator typeCtxCalc,
      WithinScopeBasicSymbolsResolver withinScopeResolver) {
    this.operatorCalculator = operatorCalculator;
    this.withinTypeResolver = withinTypeResolver;
    this.typeCtxCalc = typeCtxCalc;
    this.withinScopeResolver = withinScopeResolver;
  }

  public CommonExpressionsTypeVisitor() {
    // default values
    this(
        new TypeVisitorOperatorCalculator(),
        new WithinTypeBasicSymbolsResolver(),
        new TypeContextCalculator(),
        new WithinScopeBasicSymbolsResolver()
    );
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public void setOperatorCalculator(
      TypeVisitorOperatorCalculator operatorCalculator) {
    this.operatorCalculator = operatorCalculator;
  }

  public void setWithinTypeBasicSymbolsResolver(
      WithinTypeBasicSymbolsResolver withinTypeResolver) {
    this.withinTypeResolver = withinTypeResolver;
  }

  public void setTypeContextCalculator(TypeContextCalculator typeCtxCalc) {
    this.typeCtxCalc = typeCtxCalc;
  }

  public void setWithinScopeResolver(
      WithinScopeBasicSymbolsResolver withinScopeResolver) {
    this.withinScopeResolver = withinScopeResolver;
  }

  /**
   * @deprecated use {@link #setWithinScopeResolver}
   */
  @Deprecated
  public void setNameExpressionTypeCalculator(NameExpressionTypeCalculator resolver) {
    setWithinScopeResolver(resolver);
  }

  protected TypeVisitorOperatorCalculator getOperatorCalculator() {
    return operatorCalculator;
  }

  protected WithinTypeBasicSymbolsResolver getWithinTypeResolver() {
    return withinTypeResolver;
  }

  protected TypeContextCalculator getTypeCtxCalc() {
    return typeCtxCalc;
  }

  protected WithinScopeBasicSymbolsResolver getWithinScopeResolver() {
    return withinScopeResolver;
  }

  // Prefix

  @Override
  public void endVisit(ASTPlusPrefixExpression expr) {
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        NUMERIC_PREFIX_ERROR_CODE, expr, "+",
        getOperatorCalculator().plusPrefix(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr) {
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        NUMERIC_PREFIX_ERROR_CODE, expr, "-",
        getOperatorCalculator().minusPrefix(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Arithmetic

  @Override
  public void endVisit(ASTPlusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().plus(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMultExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().multiply(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTDivideExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().divide(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMinusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().minus(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTModuloExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().modulo(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Numeric Comparison

  @Override
  public void endVisit(ASTLessEqualExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().lessEqual(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().greaterEqual(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLessThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().lessThan(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().greaterThan(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Equality

  @Override
  public void endVisit(ASTEqualsExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        EQUALITY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().equality(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        EQUALITY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().inequality(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Conditional

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        BOOLEAN_CONDITIONAL_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().booleanAnd(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        BOOLEAN_CONDITIONAL_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().booleanOr(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    SymTypeExpression inner =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        "0xB0164", expr, "!",
        getOperatorCalculator().logicalNot(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTConditionalExpression expr) {
    SymTypeExpression cond = SymTypeRelations.normalize(
        getType4Ast().getPartialTypeOfExpr(expr.getCondition()));
    SymTypeExpression left =
        getType4Ast().getPartialTypeOfExpr(expr.getTrueExpression());
    SymTypeExpression right =
        getType4Ast().getPartialTypeOfExpr(expr.getFalseExpression());

    SymTypeExpression result;

    if (Stream.of(cond, left, right)
        .anyMatch(SymTypeExpression::isObscureType)) {
      // if any inner is obscure then error already logged
      result = createObscureType();
    }
    // condition must be boolean
    else if (!SymTypeRelations.isBoolean(cond)) {
      Log.error("0xB0165 expected '" + BasicSymbolsMill.BOOLEAN +
              "' but provided '" + cond.print() + "'",
          expr.getCondition().get_SourcePositionStart(),
          expr.getCondition().get_SourcePositionEnd()
      );
      result = createObscureType();
      // boolean conditional expression
    }
    else {
      // not normalized, as information may get missing
      result = createUnion(left, right);
    }

    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr) {
    SymTypeExpression inner =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        "0xB0175", expr, "~",
        getOperatorCalculator().bitwiseComplement(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBracketExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        getType4Ast().getPartialTypeOfExpr(expr.getExpression())
    );
  }

  // Array

  @Override
  public void endVisit(ASTArrayAccessExpression expr) {
    SymTypeExpression innerType = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression indexType = getType4Ast().getPartialTypeOfExpr(expr.getIndexExpression());
    SymTypeExpression result = TypeVisitorLifting.liftDefault(
        (innerArg, indexArg) -> calculateArrayAccess(expr, innerArg, indexArg)
    ).apply(innerType, indexType);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateArrayAccess(
      ASTArrayAccessExpression expr,
      SymTypeExpression toBeAccessed,
      SymTypeExpression indexType) {
    SymTypeExpression result;
    if (toBeAccessed.isTupleType()) {
      result = calculateArrayAccessForTuple(
          expr, toBeAccessed.asTupleType(), indexType
      );
    }
    else if (toBeAccessed.isArrayType()) {
      result = calculateArrayAccessForArray(
          expr, toBeAccessed.asArrayType(), indexType
      );
    }
    else {
      Log.error(
          "0xFDF86 trying to access expression of type "
              + toBeAccessed.printFullName()
              + " with qualifier of type "
              + indexType.printFullName()
              + " which is not applicable",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  protected SymTypeExpression calculateArrayAccessForTuple(
      ASTArrayAccessExpression expr,
      SymTypeOfTuple toBeAccessed,
      SymTypeExpression indexType
  ) {
    SymTypeExpression result;
    // for tuples, the type is directly dependent on the value provided;
    // thus, only literals are supported
    if (SymTypeRelations.isIntegralType(indexType)) {
      // todo be replaced by the interpreter as soon as available
      try {
        String indexStr =
            BasicSymbolsMill.prettyPrint(expr.getIndexExpression(), false);
        int index = Integer.parseInt(indexStr);
        if (index >= 0 && index < toBeAccessed.asTupleType().sizeTypes()) {
          result = toBeAccessed.asTupleType().getType(index);
        }
        else {
          Log.error("0xFD3F0 trying to use an index of value "
                  + index + " to access a tuple of size "
                  + toBeAccessed.asTupleType().sizeTypes()
                  + ": " + toBeAccessed.printFullName(),
              expr.get_SourcePositionStart(),
              expr.get_SourcePositionEnd()
          );
          result = createObscureType();
        }
      }
      catch (NumberFormatException e) {
        // one COULD return the union of the types included in the tuple,
        // but it is not quite clear,
        // why one would iterate over a tuple in the first case,
        // thus this case is not supported

        // it additionally does not support constants defined elsewhere,
        // e.g., myTuple[MY_ELEMENT_INDEX],
        // this would require values in the SymTab
        Log.error("0xFD3F1 trying to access a tuple"
                + " without int literal, "
                + "(currently) only integral literals are supported",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
    }
    else {
      Log.error(
          "0xFD3F3 trying a qualified access on tuple "
              + toBeAccessed.printFullName()
              + " which is not a type "
              + "applicable to qualified accesses",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  protected SymTypeExpression calculateArrayAccessForArray(
      ASTArrayAccessExpression expr,
      SymTypeArray toBeAccessed,
      SymTypeExpression indexType
  ) {
    SymTypeExpression result;
    if (SymTypeRelations.isIntegralType(indexType)) {
      result = toBeAccessed.asArrayType().cloneWithLessDim(1);
    }
    else {
      Log.error(
          "0xFD3F6 trying a qualified access on array "
              + toBeAccessed.printFullName()
              + " which is not a type "
              + "applicable to qualified accesses",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  // Field/MethodAccess

  @Override
  public void endVisit(ASTCallExpression expr) {
    // most of the time the expression within the call expression
    // will be a (qualified) name of a function.
    // here, we rely on the non-separation between functions and variables
    // (in Java, we would need `::` instead of `.` to select a method)
    // but as we support function types, the difference is nigh existent
    SymTypeExpression type;
    List<SymTypeExpression> args = new ArrayList<>();
    for (int i = 0; i < expr.getArguments().sizeExpressions(); i++) {
      args.add(getType4Ast().getPartialTypeOfExpr(expr.getArguments().getExpression(i)));
    }
    Set<SymTypeExpression> inner;
    SymTypeExpression calculatedInner = SymTypeRelations.normalize(
        getType4Ast().getPartialTypeOfExpr(expr.getExpression())
    );
    if (calculatedInner.isIntersectionType()) {
      inner = new HashSet<>(
          ((SymTypeOfIntersection)calculatedInner).getIntersectedTypeSet()
      );
    }
    else {
      inner = new HashSet<>();
      inner.add(calculatedInner);
    }

    // error already logged if Obscure
    if (inner.stream().allMatch(SymTypeExpression::isObscureType)) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    else if (args.stream().anyMatch(SymTypeExpression::isObscureType)) {
      type = SymTypeExpressionFactory.createObscureType();
    }
    // as we call, we require a function type
    else if (inner.stream().noneMatch(SymTypeExpression::isFunctionType)) {
      Log.error("0xFDABC expression does not seem to be a function, "
              + "instead the (potential) type(s) are: "
              + inner.stream()
              .map(SymTypeExpression::printFullName)
              .collect(Collectors.joining(", ")),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      Set<SymTypeOfFunction> funcs = inner.stream()
          .filter(SymTypeExpression::isFunctionType)
          .map(t -> (SymTypeOfFunction) t)
          .collect(Collectors.toSet());
      // filter out all function that do not fit the arguments
      Set<SymTypeOfFunction> callableFuncs = funcs.stream()
          .filter(f -> FunctionRelations.canBeCalledWith(f, args))
          .collect(Collectors.toSet());
      if (callableFuncs.isEmpty()) {
        Log.error("0xFDABE with " + args.size() + " argument ("
                + args.stream()
                .map(SymTypeExpression::printFullName)
                .collect(Collectors.joining(", "))
                + "), no potential function can be invoked:"
                + System.lineSeparator()
                + funcs.stream()
                .map(this::printFunctionForLog)
                .collect(Collectors.joining(System.lineSeparator())),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        type = SymTypeExpressionFactory.createObscureType();
      }
      else {
        // fix arity according to the arguments
        callableFuncs = callableFuncs.stream()
            .map(f -> f.getWithFixedArity(args.size()))
            .collect(Collectors.toSet());
        Optional<SymTypeOfFunction> mostSpecificFunction =
            FunctionRelations.getMostSpecificFunctionOrLogError(callableFuncs);
        if (mostSpecificFunction.isPresent()) {
          type = mostSpecificFunction.get().getType().deepClone();
        }
        else {
          type = createObscureType();
        }
      }
    }
    getType4Ast().setTypeOfExpression(expr, type);
  }

  /**
   * defines which results are expected of a FieldAccessExpression.
   * s.a. {@link #fieldAccessCustomTraverse(ASTFieldAccessExpression)}
   */
  protected enum FieldAccessExpectedResult {
    /**
     * A type can, but does not have to be calculated,
     * This is the case if the name can refer to a package.
     */
    OPTIONAL,
    /**
     * A type needs to be calculated, however,
     * it can either be of an expression or a type identifier.
     * This is the case if a type has already been calculated, e.g.,
     * if a.b is a type identifier, a.b.c is required to have a type.
     */
    ANY,
    /**
     * An expression type needs to be calculated.
     * This is the case if this is the topmost fieldAccessExpression, e.g.,
     * a.b.c has to be an expression if there exists no d afterwards: a.b.c.d
     */
    EXPRESSION_TYPE,
  }

  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    if (isSeriesOfNames(expr.getExpression())) {
      // done without visitor
      fieldAccessCustomTraverse(expr);
    }
    else {
      // traverse as normal
      expr.getExpression().accept(getTraverser());
    }
  }

  /**
   * custom traverse for ASTFieldAccessExpressions.
   * Normal traversing would try to calculate types,
   * however, not every ASTFieldAccessExpression is a field access expression;
   * E.g., given the expression "a.b.c.d.e", "a.b" could be a package name,
   * "a.b.c" a name of a typeId, "a.b.c.d" the name of a variable,
   * and "a.b.c.d.e" the access of field "e" in "a.b.c.d".
   * <p>
   * As such, given a series of names a.b.c,
   * up until the most outer field access expression in the series,
   * we allow to not find an expression (and thus have no type calculated).
   */
  protected void fieldAccessCustomTraverse(ASTFieldAccessExpression expr) {
    if (isSeriesOfNames(expr)) {
      if (expr.getExpression() instanceof ASTFieldAccessExpression) {
        ASTFieldAccessExpression innerFieldAccessExpr =
            (ASTFieldAccessExpression) (expr.getExpression());
        fieldAccessCustomTraverse(
            innerFieldAccessExpr
        );
        // if expression or type identifier has been found,
        // continue to require further results
        if (!getType4Ast().hasTypeOfExpression(innerFieldAccessExpr.getExpression()) &&
            !getType4Ast().hasTypeOfTypeIdentifierForName(innerFieldAccessExpr.getExpression())
        ) {
          calculateFieldAccess(innerFieldAccessExpr, FieldAccessExpectedResult.OPTIONAL);
        }
        else {
          calculateFieldAccess(innerFieldAccessExpr, FieldAccessExpectedResult.ANY);
        }
      }
      else if (expr.getExpression() instanceof ASTNameExpression) {
        ASTNameExpression nameExpr = (ASTNameExpression) (expr.getExpression());
        calculateFieldAccessFirstName(nameExpr);
      }
      else {
        Log.error("0xFD5AC internal error:"
                + "expected a series of names (a.b.c)",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
      }
    }
    else {
      Log.error("0xFD5AD internal error:"
              + "expected a series of names (a.b.c)",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
  }

  @Override
  public void endVisit(ASTFieldAccessExpression expr) {
    calculateFieldAccess(expr, FieldAccessExpectedResult.EXPRESSION_TYPE);
  }

  /**
   * implementation for endVisit.
   * One needs to state whether an expression type has to be found,
   * or a type identifier type suffices,
   * s. {@link #fieldAccessCustomTraverse(ASTFieldAccessExpression)}.
   * Note that this flag is passed to functions calculating the types,
   * simply to allow them to search differently given the knowledge
   * that the given ASTFieldAccessExpression is an actual expression.
   */
  protected void calculateFieldAccess(
      ASTFieldAccessExpression expr,
      FieldAccessExpectedResult expectedResult
  ) {
    // First, handle Obscure
    if ((getType4Ast().hasPartialTypeOfExpression(expr.getExpression()) &&
        getType4Ast().getPartialTypeOfExpr(expr.getExpression()).isObscureType()
    ) || (
        isSeriesOfNames(expr.getExpression()) &&
            getType4Ast().hasPartialTypeOfTypeIdentifierForName(expr.getExpression()) &&
            getType4Ast().getPartialTypeOfTypeIdForName(expr.getExpression()).isObscureType()
    )) {
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
      getType4Ast().setTypeOfTypeIdentifierForName(expr, SymTypeExpressionFactory.createObscureType());
      return;
    }

    // after the non-visitor traversal, types have been calculated if they exist
    Optional<SymTypeExpression> exprType;
    Optional<SymTypeExpression> typeId = Optional.empty();
    // case: expression "." name, e.g., getX().var
    if (getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      exprType = calculateExprFieldAccessOrLogError(expr, false);
    }
    // case: typeIdentifier "." name, e.g., XClass.staticVar
    // in Java, if variable exists, typeIdentifier "." name is ignored,
    // even if variable "." name does not exist
    else if (getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
      exprType = calculateTypeIdFieldAccessOrLogError(expr,
          expectedResult != FieldAccessExpectedResult.EXPRESSION_TYPE);
      // case: typeid "." typeid2 ("." name), e.g., C1.CInner.staticVar
      if (exprType.isEmpty() && expectedResult != FieldAccessExpectedResult.EXPRESSION_TYPE) {
        // always expecting a result here, as we tried expressions already
        typeId = calculateInnerTypeIdFieldAccessOrLogError(expr, false);
      }
    }
    // case: qualifier "." name
    else {
      // case: qualifier "." name as Expression
      exprType = calculateExprQNameOrLogError(expr,
          expectedResult != FieldAccessExpectedResult.EXPRESSION_TYPE);
      // case qualifier "." name as type identifier
      // this requires an outer field-access (qualifier.name.field),
      // as the end result has to be an expression
      if (exprType.isEmpty() && expectedResult == FieldAccessExpectedResult.OPTIONAL) {
        typeId = calculateTypeIdQName(expr);
      }
    }

    // store expression type
    if (exprType.isPresent()) {
      handleFieldAccessResolvedType(expr, exprType.get());
    }
    else if (expectedResult == FieldAccessExpectedResult.EXPRESSION_TYPE) {
      // error already logged
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
    // store type id
    if (typeId.isPresent()) {
      getType4Ast().setTypeOfTypeIdentifierForName(expr, typeId.get());
    }
    else if (exprType.isEmpty() && expectedResult == FieldAccessExpectedResult.ANY) {
      // error already logged
      getType4Ast().setTypeOfTypeIdentifierForName(expr, SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * field accesses are a special case with regard to the visitor structure
   * as such, this hook-point is required to enable generics support.
   */
  protected void handleFieldAccessResolvedType(
      ASTFieldAccessExpression expr,
      SymTypeExpression resolvedType
  ) {
    getType4Ast().setTypeOfExpression(expr, resolvedType);
  }

  /**
   * Part of custom traversal for field access expressions.
   * Results are always optional,
   * as the name expression could be part of a qualified name.
   */
  protected void calculateFieldAccessFirstName(ASTNameExpression expr) {
    Optional<SymTypeExpression> nameAsExprType =
        calculateExprQName(expr);
    Optional<SymTypeExpression> nameAsTypeIdType =
        calculateTypeIdQName(expr);
    if (nameAsExprType.isPresent()) {
      // here there is no need for type inference
      getType4Ast().setTypeOfExpression(expr, nameAsExprType.get());
    }
    else if (nameAsTypeIdType.isPresent()) {
      getType4Ast().setTypeOfTypeIdentifierForName(
          expr,
          nameAsTypeIdType.get()
      );
    }
  }

  // The following functions all interpret field access / name expressions

  /**
   * case: expression "." name,
   * e.g., getX().var.
   * will log an error if necessary (resultsAreOptional).
   */
  protected Optional<SymTypeExpression> calculateExprFieldAccessOrLogError(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional
  ) {
    Optional<SymTypeExpression> type = calculateExprFieldAccess(expr);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF737F given expression of type "
              + getType4Ast().getPartialTypeOfExpr(expr.getExpression()).printFullName()
              + " unable to derive the type of the access \"."
              + expr.getName() + "\". You may want to check whether"
              + System.lineSeparator()
              + "  1. The element exists in the models/included symboltables"
              + System.lineSeparator()
              + "  2. The element's access modifier is set (e.g., to public)",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    return type;
  }

  @Deprecated
  protected Optional<SymTypeExpression> calculateExprFieldAccess(
      ASTFieldAccessExpression expr) {
    return calculateExprFieldAccess(expr, false);
  }

  /**
   * calculates a.b with a being an expression,
   * e.g., getX().var
   */
  protected Optional<SymTypeExpression> calculateExprFieldAccess(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    Optional<SymTypeExpression> type;
    final String name = expr.getName();
    if (!getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      Log.error("0xFD231 internal error:"
              + "unable to find type identifier for field access",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      type = Optional.empty();
    }
    else {
      SymTypeExpression innerAsExprType =
          getType4Ast().getPartialTypeOfExpr(expr.getExpression());
      if (getWithinTypeResolver().canResolveIn(innerAsExprType)) {
        AccessModifier modifier = innerAsExprType.hasTypeInfo() ?
            getTypeCtxCalc().getAccessModifier(
                innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
            ) : AccessModifier.ALL_INCLUSION;
        type = resolveVariablesAndFunctionsWithinType(
            innerAsExprType,
            name,
            modifier,
            v -> true,
            f -> true
        );
        // Log remark about access modifier,
        // if access modifier is the reason it has not been resolved
        if (type.isEmpty() && !resultsAreOptional) {
          Optional<SymTypeExpression> potentialResult =
              resolveVariablesAndFunctionsWithinType(
                  innerAsExprType,
                  name,
                  AccessModifier.ALL_INCLUSION,
                  v -> true,
                  f -> true
              );
          if (potentialResult.isPresent()) {
            Log.warn("tried to resolve \"" + name + "\""
                    + " given expression of type "
                    + innerAsExprType.printFullName()
                    + " and symbols have been found"
                    + ", but due to the access modifiers (e.g., public)"
                    + ", nothing could be resolved",
                expr.get_SourcePositionStart(),
                expr.get_SourcePositionEnd()
            );
          }
        }
      }
      // extension point
      else {
        Log.error("0xFDB3A unexpected field access \""
                + expr.getName()
                + "\" for type "
                + innerAsExprType.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        type = Optional.empty();
      }
    }
    return type;
  }

  /**
   * case: typeIdentifier "." name,
   * e.g., XClass.staticVar.
   * will log an error if necessary (resultsAreOptional).
   */
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccessOrLogError(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional
  ) {
    Optional<SymTypeExpression> type =
        calculateTypeIdFieldAccess(expr, resultsAreOptional);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF736F given type identifier of type "
              + getType4Ast().getPartialTypeOfTypeIdForName(expr.getExpression()).printFullName()
              + " unable to derive the type of the access \"."
              + expr.getName() + "\"",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    return type;
  }

  /**
   * calculates a.b.c with a.b being a type identifier,
   * e.g., XClass.staticVar
   */
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccess(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    final String name = expr.getName();
    Optional<SymTypeExpression> type;
    if (!getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
      Log.error("0xFD232 internal error:"
              + "unable to find type identifier for field access",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      type = Optional.empty();
    }
    else {
      SymTypeExpression innerAsTypeIdType =
          getType4Ast().getPartialTypeOfTypeIdForName(expr.getExpression());
      if (getWithinTypeResolver().canResolveIn(innerAsTypeIdType)) {
        AccessModifier modifier = innerAsTypeIdType.hasTypeInfo() ?
            getTypeCtxCalc().getAccessModifier(
                innerAsTypeIdType.getTypeInfo(),
                expr.getEnclosingScope(),
                true
            ) : StaticAccessModifier.STATIC;
        type = resolveVariablesAndFunctionsWithinType(
            innerAsTypeIdType,
            name,
            modifier,
            v -> true,
            f -> true
        );
        // Log remark about access modifier,
        // if access modifier is the reason it has not been resolved
        if (type.isEmpty() && !resultsAreOptional) {
          Optional<SymTypeExpression> potentialResult =
              resolveVariablesAndFunctionsWithinType(
                  innerAsTypeIdType,
                  name,
                  AccessModifier.ALL_INCLUSION,
                  v -> true,
                  f -> true
              );
          if (potentialResult.isPresent()) {
            Log.warn("tried to resolve \"" + name + "\""
                    + " given type identifier"
                    + innerAsTypeIdType.printFullName()
                    + " and symbols have been found"
                    + ", but due to the access modifiers (e.g., static)"
                    + ", nothing could be resolved.",
                expr.get_SourcePositionStart(),
                expr.get_SourcePositionEnd()
            );
          }

        }
      }
      // extension point
      else {
        Log.error("0xFDE3A unexpected field access \""
                + expr.getName()
                + "\" for type "
                + innerAsTypeIdType.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
        type = Optional.empty();
      }
    }
    return type;
  }

  protected Optional<SymTypeExpression> calculateInnerTypeIdFieldAccessOrLogError(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional
  ) {
    Optional<SymTypeExpression> type =
        calculateInnerTypeIdFieldAccess(expr);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF736E given type identifier of type "
              + getType4Ast().getPartialTypeOfTypeIdForName(expr.getExpression()).printFullName()
              + " unable to derive the type of the access \"."
              + expr.getName() + "\"",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    return type;
  }

  /**
   * calculates a.b.c as a type identifier with a.b being a type identifier,
   * e.g., OuterClass.InnerClass.staticVariable
   */
  protected Optional<SymTypeExpression> calculateInnerTypeIdFieldAccess(
      ASTFieldAccessExpression expr) {
    final String name = expr.getName();
    Optional<SymTypeExpression> type = Optional.empty();
    if (!getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
      Log.error("0xFD233 internal error:"
              + "unable to find type identifier for field access",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    else {
      SymTypeExpression innerAsTypeIdType =
          getType4Ast().getPartialTypeOfTypeIdForName(expr.getExpression());
      if (getWithinTypeResolver().canResolveIn(innerAsTypeIdType)) {
        AccessModifier modifier = innerAsTypeIdType.hasTypeInfo() ?
            getTypeCtxCalc().getAccessModifier(
                innerAsTypeIdType.getTypeInfo(),
                expr.getEnclosingScope(),
                true
            ) : StaticAccessModifier.STATIC;
        type = getWithinTypeResolver().resolveType(
            innerAsTypeIdType,
            name,
            modifier,
            t -> true
        );
      }
      else {
        Log.error("0xFDE3A unexpected field access \""
                + expr.getName()
                + "\" for type "
                + innerAsTypeIdType.printFullName(),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
      }
    }
    return type;
  }

  /**
   * case: qName "." name,
   * e.g., package.artifact.staticVar.
   * will log an error if necessary (resultsAreOptional).
   */
  protected Optional<SymTypeExpression> calculateExprQNameOrLogError(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional
  ) {
    // case qualifier "." name as an expression
    Optional<SymTypeExpression> type = calculateExprQName(expr);
    if (type.isEmpty() && !resultsAreOptional) {
      if (isSeriesOfNames(expr)) {
        Log.error("0xF735F unable to interpret qualified name \""
                + getExprAsQName(expr).get()
                + "\" as expression",
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
      }
      else {
        // error already logged
      }
    }
    return type;
  }

  @Deprecated
  protected Optional<SymTypeExpression> calculateExprQName(
      ASTFieldAccessExpression expr) {
    return calculateExprQName(expr, false);
  }

  /**
   * calculates a.b.c as expression with a.b being a qualifier
   */
  protected Optional<SymTypeExpression> calculateExprQName(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    Optional<String> nameOpt = getExprAsQName(expr);
    Optional<SymTypeExpression> type;
    if (nameOpt.isPresent()) {
      type = getWithinScopeResolver().
          resolveNameAsExpr(
              getAsBasicSymbolsScope(expr.getEnclosingScope()),
              nameOpt.get()
          );
    }
    else {
      type = Optional.empty();
    }
    return type;
  }

  /**
   * calculates "a" as expression
   */
  protected Optional<SymTypeExpression> calculateExprQName(
      ASTNameExpression expr) {
    return getWithinScopeResolver().resolveNameAsExpr(
        getAsBasicSymbolsScope(expr.getEnclosingScope()),
        expr.getName()
    );
  }

  /**
   * calculates a.b.c as type identifier with a.b being a qualifier.
   * only evaluates qualified names without type arguments
   * s.a. {@link #getExprAsQName(ASTExpression)}
   */
  protected Optional<SymTypeExpression> calculateTypeIdQName(
      ASTFieldAccessExpression expr) {
    Optional<String> nameOpt = getExprAsQName(expr);
    Optional<SymTypeExpression> type;
    if (nameOpt.isPresent()) {
      type = getWithinScopeResolver().resolveType(
          getAsBasicSymbolsScope(expr.getEnclosingScope()),
          nameOpt.get()
      );
    }
    else {
      type = Optional.empty();
    }
    return type;
  }

  /**
   * calculates "a" as type identifier
   */
  protected Optional<SymTypeExpression> calculateTypeIdQName(
      ASTNameExpression expr) {
    return getWithinScopeResolver().resolveType(
        getAsBasicSymbolsScope(expr.getEnclosingScope()),
        expr.getName()
    );
  }

  // Helper

  /**
   * resolver helper function that searches for functions AND variables
   * in a type at the same time
   */
  protected Optional<SymTypeExpression> resolveVariablesAndFunctionsWithinType(
      SymTypeExpression innerAsExprType,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> varPredicate,
      Predicate<FunctionSymbol> funcPredicate
  ) {
    Set<SymTypeExpression> types = new HashSet<>();
    Optional<SymTypeExpression> variable =
        getWithinTypeResolver().resolveVariable(innerAsExprType,
            name,
            modifier,
            varPredicate
        );
    if (variable.isPresent()) {
      types.add(variable.get());
    }
    Collection<SymTypeOfFunction> functions =
        getWithinTypeResolver().resolveFunctions(
            innerAsExprType,
            name,
            modifier,
            funcPredicate
        );
    types.addAll(functions);
    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
  }

  /**
   * For FieldAccessExpression / CallExpression
   * given expression "." name,
   * expression may be a (qualified) name for
   * * a type (for static members)
   * * a value (global variable / function)
   * this analyses the expression and returns a qualified name if possible,
   * which _may_ be of a type / value
   * Note: Java (Spec v.20 chapter 19: Syntax) does not allow type arguments,
   * e.g., class C<T>{T t;} C<Float>.t = 3.2;
   */
  protected Optional<String> getExprAsQName(ASTExpression expr) {
    if (expr instanceof ASTNameExpression) {
      ASTNameExpression nameExpr = (ASTNameExpression) expr;
      return Optional.of(nameExpr.getName());
    }
    else if (expr instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression fieldAccessExpression =
          (ASTFieldAccessExpression) expr;
      return getExprAsQName(fieldAccessExpression.getExpression())
          .map(qualifier ->
              Names.getQualifiedName(qualifier, fieldAccessExpression.getName())
          );
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * does the expression have a form like a.b.c.d?
   */
  protected boolean isSeriesOfNames(ASTExpression expr) {
    if (expr instanceof ASTNameExpression) {
      return true;
    }
    if (expr instanceof ASTFieldAccessExpression) {
      return isSeriesOfNames(
          ((ASTFieldAccessExpression) expr).getExpression()
      );
    }
    else {
      return false;
    }
  }

  protected SymTypeExpression getTypeForInfixOrLogError(
      String errorCode, ASTInfixExpression expr, String op,
      Optional<SymTypeExpression> result,
      SymTypeExpression left, SymTypeExpression right
  ) {
    if (left.isObscureType() || right.isObscureType()) {
      return createObscureType();
    }
    else if (result.isPresent()) {
      return result.get();
    }
    else {
      // operator not applicable
      Log.error(errorCode
              + " Operator '" + op + "' not applicable to " +
              "'" + left.print() + "', '"
              + right.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  protected SymTypeExpression getTypeForPrefixOrLogError(
      String errorCode, ASTExpression expr, String prefix,
      Optional<SymTypeExpression> result, SymTypeExpression inner
  ) {
    if (inner.isObscureType()) {
      return createObscureType();
    }
    else if (result.isPresent()) {
      return result.get();
    }
    else {
      Log.error(errorCode
              + " Prefix Operator '" + prefix
              + "' not applicable to " + "'" + inner.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  protected ICommonExpressionsTypeDispatcher getTypeDispatcher() {
    return CommonExpressionsMill.typeDispatcher();
  }

  protected String printFunctionForLog(SymTypeOfFunction func) {
    String result = "";
    result += func.printFullName();
    if (func.hasSymbol()) {
      result += " (symbol: "
          + func.getSymbol().getFullName()
          + ")"
      ;
    }
    return result;
  }
}
