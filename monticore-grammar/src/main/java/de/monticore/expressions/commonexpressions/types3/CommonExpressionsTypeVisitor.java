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
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.NameExpressionTypeCalculator;
import de.monticore.types3.util.TypeContextCalculator;
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

  protected SymTypeRelations typeRelations;

  protected WithinTypeBasicSymbolsResolver withinTypeResolver;

  protected TypeContextCalculator typeCtxCalc;

  // should be the same as used in DeriveSymTypeOfExpressionBasis
  protected NameExpressionTypeCalculator nameExpressionTypeCalculator;

  protected CommonExpressionsTypeVisitor(
      SymTypeRelations typeRelations,
      WithinTypeBasicSymbolsResolver withinTypeResolver,
      TypeContextCalculator typeCtxCalc,
      NameExpressionTypeCalculator nameExpressionTypeCalculator) {
    this.typeRelations = typeRelations;
    this.withinTypeResolver = withinTypeResolver;
    this.typeCtxCalc = typeCtxCalc;
    this.nameExpressionTypeCalculator = nameExpressionTypeCalculator;
  }

  public CommonExpressionsTypeVisitor() {
    // default values
    this(
        new SymTypeRelations(),
        new WithinTypeBasicSymbolsResolver(),
        new TypeContextCalculator(),
        new NameExpressionTypeCalculator()
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

  public void setSymTypeRelations(SymTypeRelations typeRelations) {
    this.typeRelations = typeRelations;
  }

  public void setWithinTypeBasicSymbolsResolver(
      WithinTypeBasicSymbolsResolver withinTypeResolver) {
    this.withinTypeResolver = withinTypeResolver;
  }

  public void setTypeContextCalculator(TypeContextCalculator typeCtxCalc) {
    this.typeCtxCalc = typeCtxCalc;
  }

  public void setNameExpressionTypeCalculator(
      NameExpressionTypeCalculator nameExpressionTypeCalculator) {
    this.nameExpressionTypeCalculator = nameExpressionTypeCalculator;
  }

  protected SymTypeRelations getTypeRel() {
    return typeRelations;
  }

  protected WithinTypeBasicSymbolsResolver getWithinTypeResolver() {
    return withinTypeResolver;
  }

  protected TypeContextCalculator getTypeCtxCalc() {
    return typeCtxCalc;
  }

  protected NameExpressionTypeCalculator getNameExpressionTypeCalculator() {
    return nameExpressionTypeCalculator;
  }

  // Prefix

  @Override
  public void endVisit(ASTPlusPrefixExpression expr) {
    SymTypeExpression symType = calculateNumericPrefix(expr.getExpression(), "+");
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr) {
    SymTypeExpression symType = calculateNumericPrefix(expr.getExpression(), "-");
    getType4Ast().setTypeOfExpression(expr, symType);
  }

  // Arithmetic

  @Override
  public void endVisit(ASTPlusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result;

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      result = createObscureType();
    }
    // if one part of the expression is a String
    // then the whole expression is a String
    else if (getTypeRel().isString(left)) {
      result = createTypeObject(left.getTypeInfo());
    }
    else if (getTypeRel().isString(right)) {
      result = createTypeObject(right.getTypeInfo());
    }
    // no String in the expression
    // -> use the normal calculation for the basic arithmetic operators
    else {
      result = calculateArithmeticExpression(expr, expr.getOperator());
    }
    getType4Ast().setTypeOfExpression(expr, result);
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
    SymTypeExpression result;
    if (inner.isObscureType()) {
      // if inner obscure then error already logged
      result = createObscureType();
    }
    else if (getTypeRel().isBoolean(inner)) {
      result = createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    else {
      // operator not applicable
      Log.error("0xB0164 Operator '!' not applicable to "
              + "'" + inner.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTConditionalExpression expr) {
    SymTypeExpression cond =
        getType4Ast().getPartialTypeOfExpr(expr.getCondition());
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
    else if (!getTypeRel().isBoolean(cond)) {
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
    SymTypeExpression result;
    if (inner.isObscureType()) {
      // if inner obscure then error already logged
      result = createObscureType();
    }
    else if (getTypeRel().isIntegralType(inner)) {
      result = getTypeRel().numericPromotion(inner);
    }
    else {
      // operator not applicable
      Log.error("0xB0175 Operator '~' not applicable to "
              + "'" + inner.print() + "'",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      result = createObscureType();
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBracketExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        getType4Ast().getPartialTypeOfExpr(expr.getExpression())
    );
  }

  // Field/MethodAccess

  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    if (isSeriesOfNames(expr.getExpression())) {
      // done without visitor in calculateFieldAccessExpression
    }
    else {
      // traverse as normal
      expr.getExpression().accept(getTraverser());
    }
  }

  @Override
  public void endVisit(ASTFieldAccessExpression expr) {
    calculateFieldAccess(expr, false);
  }

  /**
   * implementation for endVisit,
   * s. _internal_traverse
   */
  protected void calculateFieldAccess(
      ASTFieldAccessExpression expr, boolean resultsAreOptional) {
    // handle non-visitor traversal
    if (isSeriesOfNames(expr)) {
      if (getTypeDispatcher().isASTFieldAccessExpression(expr.getExpression())) {
        calculateFieldAccess(
            getTypeDispatcher().asASTFieldAccessExpression(expr.getExpression()),
            true
        );
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

    // after the non-visitor traversal, types are calculated if they exist
    Optional<SymTypeExpression> type;
    // case: expression "." name, e.g., getX().var
    if (getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      type = calculateExprFieldAccess(expr);
      // case: typeIdentifier "." name, e.g., XClass.staticVar
      // in Java, if variable exists, typeIdentifier "." name is ignored,
      // even if variable "." name does not exist
    }
    // case: typeid "." name, e.g., MyClass.staticVar
    else if (getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
      type = calculateTypeIdFieldAccess(expr);
      // case: typeid "." typeid2 ("." name), e.g., C1.CInner.staticVar
      if (type.isEmpty() && resultsAreOptional) {
        type = calculateInnerTypeIdFieldAccess(expr);
      }
    }
    else {
      // case: qualifier "." name as Expression
      type = calculateExprQName(expr);
      // case qualifier "." name as type identifier
      // this requires an outer field-access (qualifier.name.field),
      // as the end result has to be an expression
      if (resultsAreOptional && type.isEmpty()) {
        Optional<SymTypeExpression> typeId = calculateTypeIdQName(expr);
        if (typeId.isPresent()) {
          getType4Ast().setTypeOfTypeIdentifierForName(expr, typeId.get());
        }
      }
    }

    if (type.isPresent()) {
      getType4Ast().setTypeOfExpression(expr, type.get());
    }
    else if (!resultsAreOptional) {
      if (!isSeriesOfNames(expr.getExpression())) {
        // more specific error message possible
        if (!getType4Ast().hasTypeOfExpression(expr.getExpression())
            && !getType4Ast().hasTypeOfTypeIdentifierForName(expr.getExpression())) {
          Log.error("0xF777E unable to derive type of [...] in "
                  + "\"[...]." + expr.getName() + "\"",
              expr.getExpression().get_SourcePositionStart(),
              expr.getExpression().get_SourcePositionEnd()
          );
        }
      }
      // this error message might seem rather generic.
      // issue being that we have 2-4 options to get a type from
      // and if all options fail,
      // we don't know which is the one that was expected to work
      Log.error("0xF777F unable to derive a type of [...] "
              + "for \"[...]." + expr.getName() + "\"",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
  }

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
          .filter(f -> getTypeRel().canBeCalledWith(f, args))
          .collect(Collectors.toSet());
      if (callableFuncs.isEmpty()) {
        Log.error("0xFDABE with " + args.size() + " argument ("
                + args.stream()
                .map(SymTypeExpression::printFullName)
                .collect(Collectors.joining(", "))
                + "), no potential function can be invoked:"
                + System.lineSeparator()
                + funcs.stream()
                .map(SymTypeExpression::printFullName)
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
            getTypeRel().getMostSpecificFunction(callableFuncs);
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

  // The following functions all interpret field access / name expressions

  /**
   * calculates a.b with a being an expression,
   * e.g., getX().var
   */
  protected Optional<SymTypeExpression> calculateExprFieldAccess(
      ASTFieldAccessExpression expr) {
    Set<SymTypeExpression> types = new HashSet<>();
    final String name = expr.getName();
    if (getType4Ast().hasTypeOfExpression(expr.getExpression())) {
      SymTypeExpression innerAsExprType =
          getType4Ast().getPartialTypeOfExpr(expr.getExpression());
      // object
      if (innerAsExprType.isObjectType() || innerAsExprType.isGenericType()) {

        Optional<SymTypeExpression> variable =
            getWithinTypeResolver().resolveVariable(innerAsExprType,
                name,
                getTypeCtxCalc().getAccessModifier(
                    innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
                ),
                v -> true
            );
        if (variable.isPresent()) {
          types.add(variable.get());
        }
        Collection<SymTypeOfFunction> functions =
            getWithinTypeResolver().resolveFunctions(
                innerAsExprType,
                name,
                getTypeCtxCalc().getAccessModifier(
                    innerAsExprType.getTypeInfo(), expr.getEnclosingScope()
                ),
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
   * calculates a.b.c with a.b being a type identifier,
   * e.g., XClass.staticVar
   */
  protected Optional<SymTypeExpression> calculateTypeIdFieldAccess(
      ASTFieldAccessExpression expr) {
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
      if (innerAsTypeIdType.isObjectType() || innerAsTypeIdType.isGenericType()) {
        AccessModifier modifier = getTypeCtxCalc().getAccessModifier(
            innerAsTypeIdType.getTypeInfo(),
            expr.getEnclosingScope(),
            true
        );
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
   * e.g., OuterClass.InnerClass.StaticVariable
   */
  protected Optional<SymTypeExpression> calculateInnerTypeIdFieldAccess(
      ASTFieldAccessExpression expr) {
    final String name = expr.getName();
    Optional<SymTypeExpression> type = Optional.empty();
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
      if (innerAsTypeIdType.isObjectType() || innerAsTypeIdType.isGenericType()) {
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
   * calculates a.b.c as expression with a.b being a qualifier
   */
  protected Optional<SymTypeExpression> calculateExprQName(
      ASTFieldAccessExpression expr) {
    Optional<String> nameOpt = getExprAsQName(expr);
    Optional<SymTypeExpression> type;
    if (nameOpt.isPresent()) {
      type = getNameExpressionTypeCalculator().
          typeOfNameAsExpr(
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
    return getNameExpressionTypeCalculator().typeOfNameAsExpr(
        getAsBasicSymbolsScope(expr.getEnclosingScope()),
        expr.getName()
    );
  }

  /**
   * calculates a.b.c as type identifier with a.b being a qualifier.
   * only evaluates qualified names without type arguments
   *
   * s.a. {@link #getExprAsQName(ASTExpression)}
   */
  protected Optional<SymTypeExpression> calculateTypeIdQName(
      ASTFieldAccessExpression expr) {
    Optional<String> nameOpt = getExprAsQName(expr);
    Optional<SymTypeExpression> type;
    if (nameOpt.isPresent()) {
      type = getNameExpressionTypeCalculator().
          typeOfNameAsTypeId(
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
    return getNameExpressionTypeCalculator().typeOfNameAsTypeId(
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
      ASTExpression innerExpr, String op) {
    SymTypeExpression innerType = getType4Ast().getPartialTypeOfExpr(innerExpr);
    if (innerType.isObscureType()) {
      return createObscureType();
    }
    if (!getTypeRel().isNumericType(innerType)) {
      Log.error("0xA017D Prefix Operator '" + op
              + "' not applicable to " + "'" + innerType.print() + "'",
          innerExpr.get_SourcePositionStart(),
          innerExpr.get_SourcePositionEnd()
      );
      return createObscureType();
    }
    // in Java, an evaluation of the actual value
    // would take place (if possible)
    return getTypeRel().numericPromotion(innerType);
  }

  /**
   * for <=, >=, <, >
   * calculates the resulting type
   */
  protected SymTypeExpression calculateNumericComparison(
      ASTInfixExpression expr, String op) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      return createObscureType();
    }
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    else if (getTypeRel().isNumericType(left) && getTypeRel().isNumericType(right)) {
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

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      return createObscureType();
    }
    else if (getTypeRel().isNumericType(left) && getTypeRel().isNumericType(right)) {
      return getTypeRel().numericPromotion(left, right);
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

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      return createObscureType();
    }
    else if (left.isPrimitive() || right.isPrimitive()) {
      // skip unboxing + numeric promotion if applicable
      if (getTypeRel().isNumericType(left) && getTypeRel().isNumericType(right)) {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
      }
      left = getTypeRel().unbox(left);
      right = getTypeRel().unbox(right);
    }
    if (getTypeRel().isCompatible(left, right)
        || getTypeRel().isCompatible(right, left)) {
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

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      return createObscureType();
    }
    else if (getTypeRel().isBoolean(left) && getTypeRel().isBoolean(right)) {
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
    return CommonExpressionsMill.commonExpressionsTypeDispatcher();
  }
}
