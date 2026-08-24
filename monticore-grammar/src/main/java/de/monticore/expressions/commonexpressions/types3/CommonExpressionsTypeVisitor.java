/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._symboltable.ICommonExpressionsScope;
import de.monticore.expressions.commonexpressions._util.ICommonExpressionsTypeDispatcher;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.FunctionRelations;
import de.monticore.types3.util.PostTypeCheckNodeReplacer;
import de.monticore.types3.util.TypeCheck3NameHandler;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.SymTypeRelations.normalize;

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

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * @deprecated is now a static delegate
   */
  @Deprecated(forRemoval = true)
  public void setOperatorCalculator(
      TypeVisitorOperatorCalculator operatorCalculator) {
  }

  /**
   * @deprecated is now a static delegate
   */
  @Deprecated(forRemoval = true)
  public void setWithinTypeBasicSymbolsResolver(
      WithinTypeBasicSymbolsResolver withinTypeResolver) {
  }

  /**
   * @deprecated is now a static delegate
   */
  @Deprecated(forRemoval = true)
  public void setTypeContextCalculator(TypeContextCalculator typeCtxCalc) {
  }

  /**
   * @deprecated is now a static delegate
   */
  @Deprecated(forRemoval = true)
  public void setWithinScopeResolver(
      WithinScopeBasicSymbolsResolver withinScopeResolver) {
  }

  // Prefix

  @Override
  public void endVisit(ASTPlusPrefixExpression expr) {
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        NUMERIC_PREFIX_ERROR_CODE, expr, "+",
        TypeVisitorOperatorCalculator.plusPrefix(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression expr) {
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        NUMERIC_PREFIX_ERROR_CODE, expr, "-",
        TypeVisitorOperatorCalculator.minusPrefix(inner), inner
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
        TypeVisitorOperatorCalculator.plus(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMultExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.multiply(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTDivideExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.divide(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTMinusExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.minus(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTModuloExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        ARITHMETIC_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.modulo(left, right), left, right
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
        TypeVisitorOperatorCalculator.lessEqual(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.greaterEqual(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLessThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.lessThan(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.greaterThan(left, right), left, right
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
        TypeVisitorOperatorCalculator.equality(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        EQUALITY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.inequality(left, right), left, right
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
        TypeVisitorOperatorCalculator.booleanAnd(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        BOOLEAN_CONDITIONAL_ERROR_CODE, expr, expr.getOperator(),
        TypeVisitorOperatorCalculator.booleanOr(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    SymTypeExpression inner =
        getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = getTypeForPrefixOrLogError(
        "0xB0164", expr, "!",
        TypeVisitorOperatorCalculator.logicalNot(inner), inner
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTConditionalExpression expr) {
    SymTypeExpression cond = normalize(
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
        TypeVisitorOperatorCalculator.bitwiseComplement(inner), inner
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
    Set<SymTypeExpression> inner;
    SymTypeExpression calculatedInner = normalize(
        getType4Ast().getPartialTypeOfExpr(expr.getExpression())
    );
    if (calculatedInner.isIntersectionType()) {
      inner = new LinkedHashSet<>(
          ((SymTypeOfIntersection) calculatedInner).getIntersectedTypeSet()
      );
    }
    else {
      inner = new LinkedHashSet<>();
      inner.add(calculatedInner);
    }
    if (inner.stream().allMatch(SymTypeExpression::isObscureType)) {
      // error already logged if Obscure
      type = SymTypeExpressionFactory.createObscureType();
    }
    else {
      List<SymTypeExpression> args = new ArrayList<>();
      for (int i = 0; i < expr.getArguments().sizeExpressions(); i++) {
        ASTExpression argExpr = expr.getArguments().getExpression(i);
        args.add(getType4Ast().getPartialTypeOfExpr(argExpr));
      }

      if (args.stream().anyMatch(SymTypeExpression::isObscureType)) {
        // error already logged if Obscure
        type = SymTypeExpressionFactory.createObscureType();
      }
      else {
        // as we call, we require a function type
        if (inner.stream().noneMatch(SymTypeExpression::isFunctionType)) {
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
      }
    }
    getType4Ast().setTypeOfExpression(expr, type);
  }

  @Override
  public void endVisit(ASTQualifiedNameExpression expr) {
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      return;
    }
    List<String> nameParts = expr.getNameList();
    // per default, the separator is "."
    List<String> separators = Collections.nCopies(nameParts.size() - 1, ".");

    TypeCheck3NameHandler.TypeCheck3NameHandlerResult nameTyping =
        TypeCheck3NameHandler.handleName(
            nameParts,
            separators,
            getAsBasicSymbolsScope(expr.getEnclosingScope()),
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
    nameTyping.getExprTypeOfLastNamePart().ifPresent(
        t -> handleResolvedType(expr, t)
    );
    storeReplacementExpression(expr, nameTyping);
  }

  protected void storeReplacementExpression(
      ASTQualifiedNameExpression expr,
      TypeCheck3NameHandler.TypeCheck3NameHandlerResult nameTyping
  ) {
    ASTExpression replacement = storeReplacementExpression(
        expr.getNameList(), expr.getEnclosingScope(), nameTyping
    );
    PostTypeCheckNodeReplacer.addReplacement(expr, replacement);
  }

  protected ASTExpression storeReplacementExpression(
      List<String> nameParts,
      ICommonExpressionsScope enclosingScope,
      TypeCheck3NameHandler.TypeCheck3NameHandlerResult nameTyping
  ) {
    Preconditions.checkArgument(nameParts.size() == nameTyping.size());
    ASTExpression result;
    Preconditions.checkArgument(nameTyping.size() > 0);
    Preconditions.checkArgument(
        nameTyping.getExprTypeOfLastNamePart().isPresent()
    );
    SymTypeExpression exprType = nameTyping.getExprTypeOfLastNamePart().get();
    TypeCheck3NameHandler.TypeCheck3NameHandlerResult innerNameTyping =
        nameTyping.getSublist(nameTyping.size() - 1);
    List<String> innerNameParts = nameParts.subList(0, nameParts.size() - 1);
    if (innerNameTyping.size() == 0) {
      result = CommonExpressionsMill.nameExpressionBuilder()
          .setName(nameParts.getLast())
          .build();
      result.setEnclosingScope(enclosingScope);
    }
    else if (innerNameTyping.getMCTypeOfLastNamePart().isPresent()) {
      ASTMCQualifiedType innerMCType =
          storeReplacementMCQualifiedType(innerNameParts, enclosingScope, innerNameTyping);
      result = CommonExpressionsMill.staticFieldAccessExpressionBuilder()
          .setMCType(innerMCType)
          .setName(nameParts.getLast())
          .setMCShallNotBeParsed("")
          .build();
      result.setEnclosingScope(enclosingScope);
    }
    else {
      ASTExpression innerExpr =
          storeReplacementExpression(innerNameParts, enclosingScope, innerNameTyping);
      result = CommonExpressionsMill.fieldAccessExpressionBuilder()
          .setExpression(innerExpr)
          .setName(nameParts.getLast())
          .build();
      result.setEnclosingScope(enclosingScope);
    }
    // store the type of the replacement
    getType4Ast().setTypeOfExpression(result, exprType);
    return result;
  }

  protected ASTMCQualifiedType storeReplacementMCQualifiedType(
      List<String> nameParts,
      ICommonExpressionsScope enclosingScope,
      TypeCheck3NameHandler.TypeCheck3NameHandlerResult nameTyping
  ) {
    Preconditions.checkArgument(nameTyping.size() > 0);
    Preconditions.checkArgument(
        nameTyping.getMCTypeOfLastNamePart().isPresent()
    );
    SymTypeExpression mcType = nameTyping.getMCTypeOfLastNamePart().get();
    TypeCheck3NameHandler.TypeCheck3NameHandlerResult innerNameTyping =
        nameTyping.getSublist(nameTyping.size() - 1);
    ASTMCQualifiedName qName = CommonExpressionsMill.mCQualifiedNameBuilder()
        .setPartsList(nameParts)
        .build();
    qName.setEnclosingScope(enclosingScope);
    ASTMCQualifiedType qType = CommonExpressionsMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(qName)
        .build();
    qType.setEnclosingScope(enclosingScope);
    // store the type of the replacement
    getType4Ast().setTypeOfTypeIdentifier(qType, mcType);
    return qType;
  }

  @Override
  public void endVisit(ASTFieldAccessExpression expr) {
    SymTypeExpression innerExprType =
        normalize(getType4Ast().getPartialTypeOfExpr(expr.getExpression()));
    // check obscure
    if (innerExprType.isObscureType()) {
      getType4Ast().setTypeOfExpression(expr, createObscureType());
      return;
    }

    Optional<SymTypeExpression> typeOpt =
        TypeCheck3NameHandler.calculateExprFieldAccessOrLogError(
            expr.getName(),
            ".",
            getAsBasicSymbolsScope(expr.getEnclosingScope()),
            innerExprType,
            false,
            expr.get_SourcePositionStart(),
            expr.get_SourcePositionEnd()
        );
    if (typeOpt.isPresent()) {
      handleResolvedType(expr, typeOpt.get());
    }
  }

  /**
   * generics hookpoint
   */
  protected void handleResolvedType(
      ASTExpression expr,
      SymTypeExpression resolvedType
  ) {
    getType4Ast().setTypeOfExpression(expr, resolvedType);
  }

  // Helper

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
