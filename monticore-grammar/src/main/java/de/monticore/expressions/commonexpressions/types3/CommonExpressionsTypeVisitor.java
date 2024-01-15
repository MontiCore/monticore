/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._util.CommonExpressionsTypeDispatcher;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.NameExpressionTypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorLifting;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

/**
 * This Visitor can calculate a SymTypeExpression (type)
 * for the expressions in CommonExpressions.
 * It can be combined with other expressions in your language
 */
public class CommonExpressionsTypeVisitor extends AbstractTypeVisitor
    implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  protected WithinTypeBasicSymbolsResolver withinTypeResolver;

  protected TypeContextCalculator typeCtxCalc;

  // should be the same as used in DeriveSymTypeOfExpressionBasis
  protected WithinScopeBasicSymbolsResolver withinScopeResolver;

  protected CommonExpressionsTypeVisitor(
      WithinTypeBasicSymbolsResolver withinTypeResolver,
      TypeContextCalculator typeCtxCalc,
      WithinScopeBasicSymbolsResolver withinScopeResolver) {
    this.withinTypeResolver = withinTypeResolver;
    this.typeCtxCalc = typeCtxCalc;
    this.withinScopeResolver = withinScopeResolver;
  }

  public CommonExpressionsTypeVisitor() {
    // default values
    this(
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
   * @param resolver
   * @deprecated use {@link #setWithinScopeResolver}
   */
  @Deprecated
  public void setNameExpressionTypeCalculator(NameExpressionTypeCalculator resolver) {
    setWithinScopeResolver(resolver);
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
    SymTypeExpression result = TypeVisitorLifting.liftDefault(
        (innerType) -> calculateNumericPrefix(expr.getExpression(), "+", innerType)
    ).apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr) {
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting.liftDefault(
        (innerType) -> calculateNumericPrefix(expr.getExpression(), "-", innerType)
    ).apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Arithmetic

  @Override
  public void endVisit(ASTPlusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((leftPar, rightPar) -> calculatePlusExpression(expr, leftPar, rightPar))
        .apply(left, right);

    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculatePlusExpression(ASTPlusExpression expr, SymTypeExpression left, SymTypeExpression right) {
    SymTypeExpression result;
    // if one part of the expression is a String
    // then the whole expression is a String
    if (SymTypeRelations.isString(left)) {
      result = createTypeObject(left.getTypeInfo());
    }
    else if (SymTypeRelations.isString(right)) {
      result = createTypeObject(right.getTypeInfo());
    }
    // no String in the expression
    // -> use the normal calculation for the basic arithmetic operators
    else {
      result = calculateArithmeticExpression(expr, expr.getOperator());
    }
    return result;
  }

  @Override
  public void endVisit(ASTMultExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateArithmeticExpression(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTDivideExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateArithmeticExpression(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTMinusExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateArithmeticExpression(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTModuloExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateArithmeticExpression(expr, expr.getOperator())
    );
  }

  // Numeric Comparison

  @Override
  public void endVisit(ASTLessEqualExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateNumericComparison(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateNumericComparison(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTLessThanExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateNumericComparison(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateNumericComparison(expr, expr.getOperator())
    );
  }

  // Equality

  @Override
  public void endVisit(ASTEqualsExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateEquality(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateEquality(expr, expr.getOperator())
    );
  }

  // Conditional

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateConditionalBooleanOp(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        calculateConditionalBooleanOp(expr, expr.getOperator())
    );
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    SymTypeExpression inner =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((symType) -> calculateLogicalNotExpression(expr, symType))
        .apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateLogicalNotExpression(
      ASTLogicalNotExpression expr,
      SymTypeExpression inner) {
    if (SymTypeRelations.isBoolean(inner)) {
      return createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      // operator not applicable
      Log.error("0xB0164 Operator '!' not applicable to "
              + "'" + inner.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
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
    SymTypeExpression result = TypeVisitorLifting.liftDefault(
        (symType) -> calculateBooleanNotExpression(expr, symType)
    ).apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateBooleanNotExpression(
      ASTBooleanNotExpression expr,
      SymTypeExpression inner) {
    if (SymTypeRelations.isIntegralType(inner)) {
      return SymTypeRelations.numericPromotion(inner);
    }
    else {
      // operator not applicable
      Log.error("0xB0175 Operator '~' not applicable to "
              + "'" + inner.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
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
              + "with qualifier of type "
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
    if (getType4Ast().getPartialTypeOfExpr(expr.getExpression())
        .isIntersectionType()) {
      inner = new HashSet<>(
          ((SymTypeOfIntersection) getType4Ast()
              .getPartialTypeOfExpr(expr.getExpression())
          ).getIntersectedTypeSet()
      );
    }
    else {
      inner = new HashSet<>();
      inner.add(getType4Ast().getPartialTypeOfExpr(expr.getExpression()));
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
            FunctionRelations.getMostSpecificFunction(callableFuncs);
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
      if (getTypeDispatcher().isASTFieldAccessExpression(expr.getExpression())) {
        ASTFieldAccessExpression innerFieldAccessExpr =
            getTypeDispatcher().asASTFieldAccessExpression(expr.getExpression());
        fieldAccessCustomTraverse(innerFieldAccessExpr);
        // if expression or type identifier has been found,
        // continue to require further results
        boolean resultsAreOptional =
            !getType4Ast().hasTypeOfExpression(expr.getExpression()) &&
                !getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression());
        calculateFieldAccess(innerFieldAccessExpr, resultsAreOptional);
      }
      else if (getTypeDispatcher().isASTNameExpression(expr.getExpression())) {
        ASTNameExpression nameExpr = getTypeDispatcher().asASTNameExpression(expr.getExpression());
        Optional<SymTypeExpression> nameAsExprType =
            calculateExprQName(nameExpr);
        Optional<SymTypeExpression> nameAsTypeIdType =
            calculateTypeIdQName(nameExpr);
        if (nameAsExprType.isPresent()) {
          getType4Ast().setTypeOfExpression(nameExpr, nameAsExprType.get());
        }
        else if (nameAsTypeIdType.isPresent()) {
          getType4Ast().setTypeOfTypeIdentifierForName(
              nameExpr,
              nameAsTypeIdType.get()
          );
        }
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
    calculateFieldAccess(expr, false);
  }

  /**
   * implementation for endVisit.
   *
   * @param resultsAreOptional whether an expression type has to be found,
   *                           s. {@link #fieldAccessCustomTraverse(ASTFieldAccessExpression)}.
   *                           Note that this flag is passed to functions
   *                           calculating the types, simply to allow them
   *                           to search differently given the knowledge
   *                           that the given ASTFieldAccessExpression
   *                           is an actual expression
   */
  protected void calculateFieldAccess(
      ASTFieldAccessExpression expr, boolean resultsAreOptional) {
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
      exprType = calculateTypeIdFieldAccessOrLogError(expr, resultsAreOptional);
      // case: typeid "." typeid2 ("." name), e.g., C1.CInner.staticVar
      if (exprType.isEmpty() && resultsAreOptional) {
        typeId = calculateInnerTypeIdFieldAccess(expr);
      }
    }
    // case: qualifier "." name
    else {
      // case: qualifier "." name as Expression
      exprType = calculateExprQNameOrLogError(expr, resultsAreOptional);
      // case qualifier "." name as type identifier
      // this requires an outer field-access (qualifier.name.field),
      // as the end result has to be an expression
      if (exprType.isEmpty() && resultsAreOptional) {
        typeId = calculateTypeIdQName(expr);
      }
    }

    // store expression type
    if (exprType.isPresent()) {
      getType4Ast().setTypeOfExpression(expr, exprType.get());
    }
    else if (!resultsAreOptional) {
      // error already logged
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
    // store type id
    if (typeId.isPresent()) {
      getType4Ast().setTypeOfTypeIdentifierForName(expr, typeId.get());
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
              + expr.getName() + "\"",
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
    Set<SymTypeExpression> types = new HashSet<>();
    final String name = expr.getName();
    if (!getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      Log.error("0xFD231 internal error:"
              + "unable to find type identifier for field access",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    else {
      SymTypeExpression innerAsExprType =
          getType4Ast().getPartialTypeOfExpr(expr.getExpression());
      if (getWithinTypeResolver().canResolveIn(innerAsExprType)) {
        AccessModifier modifier = innerAsExprType.hasTypeInfo() ?
            getTypeCtxCalc().getAccessModifier(
                innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
            ) : AccessModifier.ALL_INCLUSION;
        Optional<SymTypeExpression> variable =
            getWithinTypeResolver().resolveVariable(innerAsExprType,
                name,
                modifier,
                v -> true
            );
        if (variable.isPresent()) {
          types.add(variable.get());
        }
        Collection<SymTypeOfFunction> functions =
            getWithinTypeResolver().resolveFunctions(
                innerAsExprType,
                name,
                modifier,
                f -> true
            );
        types.addAll(functions);
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
      }
    }
    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
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
    Optional<SymTypeExpression> type = calculateTypeIdFieldAccess(expr);
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

  @Deprecated
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccess(
      ASTFieldAccessExpression expr) {
    return calculateTypeIdFieldAccess(expr, false);
  }

  /**
   * calculates a.b.c with a.b being a type identifier,
   * e.g., XClass.staticVar
   */
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccess(
      ASTFieldAccessExpression expr,
      boolean resultsAreOptional) {
    final String name = expr.getName();
    Set<SymTypeExpression> types = new HashSet<>();
    if (!getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
      Log.error("0xFD232 internal error:"
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
        Optional<SymTypeExpression> variable =
            getWithinTypeResolver().resolveVariable(
                innerAsTypeIdType,
                name,
                modifier,
                v -> true
            );
        variable.ifPresent(types::add);
        Collection<SymTypeOfFunction> functions =
            getWithinTypeResolver().resolveFunctions(
                innerAsTypeIdType,
                name,
                modifier,
                f -> true
            );
        types.addAll(functions);
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
      }
    }
    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
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
            getTypeCtxCalc().getAccessModifier(
                innerAsTypeIdType.getTypeInfo(),
                expr.getEnclosingScope(),
                true
            ),
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
    if (getTypeDispatcher().isASTNameExpression(expr)) {
      ASTNameExpression nameExpr = (ASTNameExpression) expr;
      return Optional.of(nameExpr.getName());
    }
    else if (getTypeDispatcher().isASTFieldAccessExpression(expr)) {
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
    if (getTypeDispatcher().isASTNameExpression(expr)) {
      return true;
    }
    if (getTypeDispatcher().isASTFieldAccessExpression(expr)) {
      return isSeriesOfNames(
          getTypeDispatcher().asASTFieldAccessExpression(expr).getExpression()
      );
    }
    else {
      return false;
    }
  }

  protected SymTypeExpression calculateNumericPrefix(
      ASTExpression innerExpr, String op, SymTypeExpression innerType) {
    if (!SymTypeRelations.isNumericType(innerType)) {
      Log.error("0xA017D Prefix Operator '" + op
              + "' not applicable to " + "'" + innerType.print() + "'",
          innerExpr.get_SourcePositionStart(),
          innerExpr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
    // in Java, an evaluation of the actual value
    // would take place (if possible)
    return SymTypeRelations.numericPromotion(innerType);
  }

  /**
   * for <=, >=, <, >
   * calculates the resulting type
   */
  protected SymTypeExpression calculateNumericComparison(
      ASTInfixExpression expr, String op) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    return TypeVisitorLifting.liftDefault((leftPar, rightPar) ->
        calculateNumericComparison(expr, op, leftPar, rightPar)
    ).apply(left, right);
  }

  protected SymTypeExpression calculateNumericComparison(
      ASTInfixExpression expr, String op,
      SymTypeExpression left, SymTypeExpression right) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (SymTypeRelations.isNumericType(left) && SymTypeRelations.isNumericType(right)) {
      return createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      // operator not applicable
      Log.error("0xB0167 Operator '" + op + "' not applicable to "
              + "'" + left.print() + "', '"
              + right.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  /**
   * for +, -, *, /, %
   * + -> String not supported
   * calculates the resulting type
   */
  protected SymTypeExpression calculateArithmeticExpression(
      ASTInfixExpression expr, String op) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    return TypeVisitorLifting.liftDefault((SymTypeExpression leftPar, SymTypeExpression rightPar) ->
        calculateArithmeticExpression(expr, op, leftPar, rightPar)
    ).apply(left, right);
  }

  protected SymTypeExpression calculateArithmeticExpression(ASTInfixExpression expr, String op, SymTypeExpression left, SymTypeExpression right) {
    if (SymTypeRelations.isNumericType(left) && SymTypeRelations.isNumericType(right)) {
      return SymTypeRelations.numericPromotion(left, right);
    }
    else {
      // operator not applicable
      Log.error("0xB0163 Operator '" + op + "' not applicable to " +
              "'" + left.print() + "', '"
              + right.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  /**
   * for ==, !=
   * calculates the resulting type
   */
  protected SymTypeExpression calculateEquality(
      ASTInfixExpression expr, String op) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    return TypeVisitorLifting.liftDefault((leftPar, rightPar) ->
        calculateEquality(expr, op, leftPar, rightPar)
    ).apply(left, right);
  }

  /**
   * for ==, !=
   * calculates the resulting type
   */
  protected SymTypeExpression calculateEquality(
      ASTInfixExpression expr, String op,
      SymTypeExpression left, SymTypeExpression right) {

    if (left.isPrimitive() || right.isPrimitive()) {
      // skip unboxing + numeric promotion if applicable
      if (SymTypeRelations.isNumericType(left) && SymTypeRelations.isNumericType(right)) {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
      }
      left = SymTypeRelations.unbox(left);
      right = SymTypeRelations.unbox(right);
    }
    if (SymTypeRelations.isCompatible(left, right)
        || SymTypeRelations.isCompatible(right, left)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      Log.error("0xB0166 Operator '" + op + "' not applicable to " +
              "'" + left.print() + "', '"
              + right.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  /**
   * for &&, ||
   * calculates the resulting type
   */
  protected SymTypeExpression calculateConditionalBooleanOp(
      ASTInfixExpression expr, String op) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    return TypeVisitorLifting.liftDefault((leftPar, rightPar) ->
        calculateConditionalBooleanOp(expr, op, leftPar, rightPar)
    ).apply(left, right);
  }

  protected SymTypeExpression calculateConditionalBooleanOp(
      ASTInfixExpression expr, String op,
      SymTypeExpression left, SymTypeExpression right) {
    if (SymTypeRelations.isBoolean(left) && SymTypeRelations.isBoolean(right)) {
      return createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      // operator not applicable
      Log.error("0xB0113 Operator '" + op + "' not applicable to " +
              "'" + left.print() + "', '"
              + right.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
  }

  protected CommonExpressionsTypeDispatcher getTypeDispatcher() {
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
