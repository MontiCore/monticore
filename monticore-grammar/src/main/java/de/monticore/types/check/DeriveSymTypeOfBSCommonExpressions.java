/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.types.check.helpers.*;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.*;

public class DeriveSymTypeOfBSCommonExpressions extends AbstractDeriveFromExpression implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  protected SubExprNameExtractor subExprNameExtractor;

  protected DefiningSymbolSetter definingSymbolSetter;

  public DeriveSymTypeOfBSCommonExpressions() {
    this(new SubExprNameExtractor4CommonExpressions(), new DefiningSymbolSetter4CommonExpressions());
  }

  public DeriveSymTypeOfBSCommonExpressions(SubExprNameExtractor subExprNameExtractor,
                                            DefiningSymbolSetter definingSymbolSetter) {
    this.subExprNameExtractor = subExprNameExtractor;
    this.definingSymbolSetter = definingSymbolSetter;
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public SubExprNameExtractor getSubExprNameExtractor() {
    return subExprNameExtractor;
  }

  public void setSubExprNameExtractor(SubExprNameExtractor subExprNameExtractor) {
    this.subExprNameExtractor = subExprNameExtractor;
  }

  public DefiningSymbolSetter getDefiningSymbolSetter() {
    return definingSymbolSetter;
  }

  public void setDefiningSymbolSetter(DefiningSymbolSetter definingSymboLSetter) {
    this.definingSymbolSetter = definingSymboLSetter;
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = numericPrefix(expr.getExpression(), "+", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = numericPrefix(expr.getExpression(), "-", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression numericPrefix(ASTExpression expr, String op, SourcePosition pos) {
    this.getTypeCheckResult().reset();
    expr.accept(this.getTraverser());
    TypeCheckResult inner = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!inner.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0174", expr.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (inner.getResult().isObscureType()) {
      // if inner obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else {
      return numericPrefix(inner.getResult(), op, pos);
    }
  }

  protected SymTypeExpression numericPrefix(SymTypeExpression inner, String op, SourcePosition pos) {
    if (isDouble(inner)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
    } else if (isFloat(inner)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
    } else if (isLong(inner)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
    } else if (isIntegralType(inner)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    } else {
      Log.error("0xA0175 Operator '" + op + "' not applicable to " + "'" + inner.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculatePlusExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculatePlusExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    // if one part of the expression is a String then the whole expression is a String
    if(isString(left)) {
      return SymTypeExpressionFactory.createTypeObject(left.getTypeInfo());
    } else if (isString(right)) {
      return SymTypeExpressionFactory.createTypeObject(right.getTypeInfo());
    } else {
      // no String in the expression -> use the normal calculation for the basic arithmetic operators
      return calculateArithmeticExpression(left, right, "+", pos);
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateMultExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateMultExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateArithmeticExpression(left, right, "*", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateDivideExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateDivideExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateArithmeticExpression(left, right, "/", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMinusExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateMinusExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateMinusExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateArithmeticExpression(left, right, "-", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTModuloExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateModuloExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateModuloExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateArithmeticExpression(left, right, "%", pos);
  }


  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.lessEqual(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression lessEqual(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeCompare(left, right, "<=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.greaterEqual(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression greaterEqual(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeCompare(left, right, ">=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.less(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression less(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeCompare(left, right, "<", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateGreaterThanExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateGreaterThanExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeCompare(left, right, ">", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateEqualsExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateEqualsExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeLogical(left, right, "==", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateNotEqualsExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateNotEqualsExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateTypeLogical(left, right, "!=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateBooleanAndOpExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateBooleanAndOpExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateLogicalOrOpAndOp(left, right, "&&", pos);
  }

  @Override
  public void traverse(ASTBooleanOrOpExpression expr) {
    SymTypeExpression left = acceptThisAndReturnSymTypeExpression(expr.getLeft());
    SymTypeExpression right = acceptThisAndReturnSymTypeExpression(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.calculateBooleanOrOpExpression(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression calculateBooleanOrOpExpression(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return calculateLogicalOrOpAndOp(left, right, "||", pos);
  }

  protected SymTypeExpression calculateLogicalOrOpAndOp(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
    if (isBoolean(left) && isBoolean(right)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    } else {
      // else operator not applicable
      Log.error("0xA0167 Operator '" + op + "' not applicable to " + "'" + left.print() + "', '" + right.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.derive(expr);

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression derive(ASTLogicalNotExpression expr) {
    // calculate the type of the inner expressions
    this.getTypeCheckResult().reset();
    expr.getExpression().accept(this.getTraverser());
    TypeCheckResult inner = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!inner.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0170", expr.getExpression().get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (inner.getResult().isObscureType()) {
      // if inner obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else {
      // else check with signature
      return logicalNot(inner.getResult(), expr.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression logicalNot(SymTypeExpression inner, SourcePosition pos) {
    if (isBoolean(inner)) {
     return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    } else {
      Log.error("0xA0171 Operator '!' not applicable to " + "'" + inner.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression condition = this.acceptThisAndReturnSymTypeExpression(expr.getCondition());
    SymTypeExpression first = this.acceptThisAndReturnSymTypeExpression(expr.getTrueExpression());
    SymTypeExpression second = this.acceptThisAndReturnSymTypeExpression(expr.getFalseExpression());

    getTypeCheckResult().reset();
    getTypeCheckResult().setResult(this.calculateConditionalExpressionType(expr, condition, first, second));
  }

  protected SymTypeExpression calculateConditionalExpressionType(ASTConditionalExpression expr,
                                                                 SymTypeExpression conditionResult,
                                                                 SymTypeExpression trueResult,
                                                                 SymTypeExpression falseResult) {
    if (!conditionResult.isObscureType() && !isBoolean(conditionResult)) {
      // if obscure then error already logged
      // else condition must be boolean
      Log.error("0xA0165 Expected '" + BasicSymbolsMill.BOOLEAN + "' but provided '" + conditionResult.print() + "'",
        expr.getCondition().get_SourcePositionStart(), expr.getCondition().get_SourcePositionEnd());
    }

    if (compatible(trueResult, falseResult)) {
      return trueResult;
    } else if (compatible(falseResult, trueResult)) {
     return falseResult;
    } else {
      SymTypeExpression inner = getBinaryNumericPromotion(trueResult, falseResult);
      if (inner.isObscureType()) {
        // binary numeric promotion does not log error
        Log.error("0xA0164 Resulting types '" + trueResult + "' and '" + falseResult + "' of operator ? are incompatible",
          expr.getTrueExpression().get_SourcePositionStart(), expr.getFalseExpression().get_SourcePositionEnd());
      }
      return inner;
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanNotExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.derive(expr);

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression derive(ASTBooleanNotExpression expr) {
    // calculate the type of the inner expressions
    this.getTypeCheckResult().reset();
    expr.getExpression().accept(this.getTraverser());
    TypeCheckResult inner = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!inner.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0172", expr.getExpression().get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (inner.getResult().isObscureType()) {
      // if inner obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else {
      // else check with signature
      return booleanNot(inner.getResult(), expr.get_SourcePositionStart());
    }
  }

  protected SymTypeExpression booleanNot(SymTypeExpression inner, SourcePosition pos) {
    if (isIntegralType(inner)) {
      if (isLong(inner)) {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
      } else {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
      }
    } else {
      Log.error("0xA0173 Operator '~' not applicable to " + "'" + inner.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    SubExprNameExtractionResult extractedNames = getSubExprNameExtractor().calculateNameParts(expr);

    if(extractedNames.resultIsValidName() && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      List<ExprToNamePair> nameParts = extractedNames.getNamePartsIfValid().get();
      calculateNamingChainFieldAccess(expr, nameParts);
    } else {
      calculateArithmeticFieldAccessExpression(expr);
    }
  }

  /**
   * Calculate the type result of FieldAccessExpressions that represent qualified names and cascading field accesses.
   * E.g., pac.kage.Type.staticMember, or, localField.innerField.furtherNestedField.
   * But not: pac.kage.Type.staticMethod().innerField, as here <i>innerField</i> is not only qualified by names, but
   * it is based on the access of a value returned by a CallExpression.
   * @param expr The only valid sub expressions of the FieldAccessExpression are other FieldAccessExpressions, and
   *             a {@link ASTNameExpression} that is the end of the field access chain.
   */
  protected void calculateNamingChainFieldAccess(ASTFieldAccessExpression expr,
                                                 List<ExprToNamePair> nameParts) {


    // We will incrementally try to build our result:
    // We will start with the first name part and check whether it resolves to an entity.
    // Then we will check whether further field accesses are (nested) accesses on that entity's members.
    // When there is no such entity, or it does not have a member we were looking for, then we try to resolve the
    // qualified name up to the part of the name where we currently are.

    getTypeCheckResult().reset();
    for(int i = 0; i < nameParts.size(); i++) {
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
        calculateFieldAccess((ASTFieldAccessExpression) nameParts.get(i).getExpression(), true);
      } else {
        calculatedQualifiedEntity(nameParts.subList(0, i + 1));
      }
    }

    if(!getTypeCheckResult().isPresentResult() || getTypeCheckResult().getResult().isObscureType()) {
      logError("0xA0241", expr.get_SourcePositionStart());
    }
  }

  /**
   * Calculate the type result of FieldAccessExpressions that do not represent qualified names. (E.g. `new Foo().bar`)
   */
  protected void calculateArithmeticFieldAccessExpression(ASTFieldAccessExpression expr) {
    expr.getExpression().accept(getTraverser());
    if (getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      calculateFieldAccess(expr, false);
    } // else do nothing (as the type check result is already absent or obscure).
  }

  /**
   * Calculates the type result of the field access expression, given that the type result of the accessed entity's
   * owner has already been computed (and is accessible via getTypeCheckResult()).
   * @param quiet Prevents the logging of errors if no entity is found that could be accessed, i.e., if the field access
   *              is invalid and the calculation of a result is not possible.
   */
  protected void calculateFieldAccess(ASTFieldAccessExpression expr,
                                      boolean quiet) {
    TypeCheckResult fieldOwner = getTypeCheckResult().copy();
    SymTypeExpression fieldOwnerExpr = fieldOwner.getResult();
    TypeSymbol fieldOwnerSymbol = fieldOwnerExpr.getTypeInfo();
    if (fieldOwnerSymbol instanceof TypeVarSymbol && !quiet) {
      Log.error("0xA0321 The type " + fieldOwnerSymbol.getName() + " is a type variable and cannot have methods and attributes");
    }
    //search for a method, field or type in the scope of the type of the inner expression
    List<VariableSymbol> fieldSymbols = getCorrectFieldsFromInnerType(fieldOwnerExpr, expr);
    Optional<TypeSymbol> typeSymbolOpt = fieldOwnerSymbol.getSpannedScope().resolveType(expr.getName());
    Optional<TypeVarSymbol> typeVarOpt = fieldOwnerSymbol.getSpannedScope().resolveTypeVar(expr.getName());

    if (!fieldSymbols.isEmpty()) {
      //cannot be a method, test variable first
      //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
      //if the last result is a type then filter for static field symbols
      if (fieldOwner.isType()) {
        fieldSymbols = filterModifiersVariables(fieldSymbols);
      }
      if (fieldSymbols.size() != 1) {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1236", expr.get_SourcePositionStart());
        }
      }
      if (!fieldSymbols.isEmpty()) {
        VariableSymbol var = fieldSymbols.get(0);
        expr.setDefiningSymbol(var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        getTypeCheckResult().setResult(type);
      }
    } else if (typeVarOpt.isPresent()) {
      //test for type var first
      TypeVarSymbol typeVar = typeVarOpt.get();
      if(checkModifierType(typeVar)){
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVariable(typeVar);
        expr.setDefiningSymbol(typeVar);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else{
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1306", expr.get_SourcePositionStart());
        }
      }
    } else if (typeSymbolOpt.isPresent()) {
      //no variable found, test type
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      if (checkModifierType(typeSymbol)) {
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        expr.setDefiningSymbol(typeSymbol);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1303", expr.get_SourcePositionStart());
        }
      }
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      if(!quiet) {
        logError("0xA1317", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct variables/fields from a type based on their modifiers
   */
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), getTypeCheckResult().isType(), true);
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected boolean checkModifierType(TypeSymbol typeSymbol){
    return true;
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<VariableSymbol> filterModifiersVariables(List<VariableSymbol> variableSymbols) {
    return variableSymbols;
  }

  /**
   * Tries to resolve the given name parts to a variable, type variable, or type and if a symbol is found, then
   * it(s type) is set as the current type check result.
   * If no symbol is found, then nothing happens (no error logged, no altering of the type check result).
   * If multiple fields are found, then the result is set to obscure, and an error is logged.
   * Variables take precedence over types variables that take precedence over
   * types.
   * @param nameParts Expressions that represent a qualified identification of a {@link VariableSymbol},
   *                  {@link TypeVarSymbol}, or {@link TypeSymbol}. Therefore, the list that must contain a
   *                  {@code NameExpression} at the beginning, followed only by {@code FieldAccessExpression}s.
   */
  protected void calculatedQualifiedEntity(List<ExprToNamePair> nameParts) {
    List<String> namePartStrings = nameParts.stream().map(ExprToNamePair::getName).collect(Collectors.toList());
    String qualName = String.join(".", namePartStrings);
    ASTExpression lastExpr = nameParts.get(nameParts.size() - 1).getExpression();

    List<VariableSymbol> fieldSymbols = getScope(lastExpr.getEnclosingScope()).resolveVariableMany(qualName);
    Optional<TypeSymbol> typeSymbolOpt = getScope(lastExpr.getEnclosingScope()).resolveType(qualName);
    Optional<TypeVarSymbol> typeVarOpt = getScope(lastExpr.getEnclosingScope()).resolveTypeVar(qualName);

    if (!fieldSymbols.isEmpty()) {
      if (fieldSymbols.size() != 1) {
        logError("0xA1236", lastExpr.get_SourcePositionStart());
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      } else {
        VariableSymbol var = fieldSymbols.get(0);
        definingSymbolSetter.setDefiningSymbol(lastExpr, var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        getTypeCheckResult().setResult(type);
      }

    } else if (typeVarOpt.isPresent()) {
      TypeVarSymbol typeVar = typeVarOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeVariable(typeVar);
      definingSymbolSetter.setDefiningSymbol(lastExpr, typeVar);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);

    } else if (typeSymbolOpt.isPresent()) {
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
      definingSymbolSetter.setDefiningSymbol(lastExpr, typeSymbol);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    SubExprNameExtractionResult extractedNames = getSubExprNameExtractor().calculateNameParts(expr.getExpression());

    Optional<String> methodName = extractedNames.getLastName();

    if (methodName.isEmpty()) {
      // We do not have any method name. The expression looks like `(2+3)()` and is parsed as a CallExpression that has
      // the syntactic form of `CallExpression = Expression Arguments;`
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA1237", expr.get_SourcePositionStart());
      return;
    }

    List<SymTypeExpression> arguments = calculateArguments(expr, methodName.get());

    if (extractedNames.resultIsValidName() && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      calculateNamingChainCallExpression(expr, extractedNames.getNamePartsIfValid().get(), arguments);
    } else {
      calculateArithmeticCallExpression(expr, methodName.get(), extractedNames.getNamePartsRaw(), arguments);
    }
  }

  /**
   * Calculate the type result of call expressions that represent fully qualified method calls (like when calling a
   * static method: {@code pac.kage.Type.staticMethod()}) and methodCalls on cascading field accesses (e.g.,
   * {@code localField.innerField.instanceMethod()}). But not:
   * {@code pac.kage.Type.staticMethod().innerField.instanceMethod()}, as here <i>instanceMethod</i> is not only
   * qualified by names, but it is based on the access of a value returned by a CallExpression.
   *
   * @param expr The call expression itself
   * @param nameParts The name parts of the method in their order of appearance. Provide both their AST version, and
   *                  their String version!
   * @param argTypes the types of the arguments of the method
   */
  protected void calculateNamingChainCallExpression(ASTCallExpression expr,
                                                    List<ExprToNamePair> nameParts,
                                                    List<SymTypeExpression> argTypes) {
    List<ASTExpression> astNameParts = nameParts.stream().map(ExprToNamePair::getExpression).collect(Collectors.toList());
    List<String> stringNameParts = nameParts.stream().map(ExprToNamePair::getName).collect(Collectors.toList());

    String methodName = stringNameParts.get(stringNameParts.size() - 1);

    // We will incrementally try to build our result:
    // We will start with the first name part and check whether it resolves to an entity.
    // Then we will check whether further field accesses are (nested) accesses on that entity's members.
    // When there is no such entity, or it does not have a member we were looking for, then we try to resolve the
    // qualified name up to the part of the name where we currently are.
    // We terminate *before* the last name part, as the last name part must resolve to a method and not a type or field.

    getTypeCheckResult().reset();
    for(int i = 0; i < astNameParts.size() - 1; i++) {
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
        calculateFieldAccess((ASTFieldAccessExpression) astNameParts.get(i), true);
      } else {
        calculatedQualifiedEntity(nameParts.subList(0, i + 1));
      }
    }

    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      calculateOwnedCallExpression(expr, methodName, argTypes);
    } else {
      // Check whether we have a fully qualified method. Local method calls will also end in this program branch.
      String qualName = String.join(".", stringNameParts);
      calculateQualifiedMethod(qualName, expr, argTypes);
    }
  }

  /**
   * Calculates the type result of the expression, given that it is not a simple, or qualified method access. E.g.,
   * this method calculates the result of {@code "FooBar".substring(0, 3)}, or
   * {@code ( foo ? new LinkedList<String>() : new ArrayList<String>() ).add("bar")}. On the other hand, this method is
   * not suited for {@code pac.kage.Owner.staticMethod()}, or {@code isInt()} (a local Method name).
   * Use {@link #calculateNamingChainCallExpression(ASTCallExpression, List, List)} in these cases.
   */
  protected void calculateArithmeticCallExpression(ASTCallExpression expr,
                                                   String methodName,
                                                   List<ExprToOptNamePair> methodCallParts,
                                                   List<SymTypeExpression> argumentTypes) {
    if(methodCallParts.size() < 2) {
      Log.error("0xA1240 (Internal error) call expression just consists of a method name, but the program " +
        "flow ended in the branch that calculates call expressions on method chains and similar.");
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }

    getTypeCheckResult().reset();
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());

    ASTExpression methodOwner = methodCallParts.get(methodCallParts.size() - 2).getExpression();
    methodOwner.accept(getTraverser());
    calculateOwnedCallExpression(expr, methodName, argumentTypes);
  }

  /**
   * Calculates the type result of the call expression, given that the type result of the method owner has already been
   * computed (and is accessible via getTypeCheckResult()), and that the type of its arguments has already been
   * computed.
   */
  protected void calculateOwnedCallExpression(ASTCallExpression expr, String methodName, List<SymTypeExpression> args) {
    if(!getTypeCheckResult().isPresentResult()
      || getTypeCheckResult().getResult().isObscureType()) {
      return;
    }

    SymTypeExpression methodOwnerExpr = getTypeCheckResult().getResult();
    // Filter based on the method modifiers
    List<FunctionSymbol> methodList = getCorrectMethodsFromInnerType(methodOwnerExpr, expr, methodName);

    if(getTypeCheckResult().isType()) {
      methodList = filterModifiersFunctions(methodList);
    }

    if(methodList.isEmpty()) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA2239", expr.get_SourcePositionStart());
    } else {
      calculateMethodReturnTypeBasedOnSignature(methodList, expr, args);
    }
  }

  /**
   * Checks whether there exists a method symbol with the name given by {@code qualName} with matching argument types.
   * If such a method exists, it's return type is set as the type check result.
   * @param qualName Qualified name of the method symbol to look for. Can also be a simple name without qualification.
   * @param callExpr The call expression of the method call. Needed as entry point to the symbol table to resolve
   *                 method symbols.
   * @param argTypes The types of the method arguments. Are allowed to be obscure, but in this case the method will also
   *                 always set *obscure* to be the method's type. Nevertheless, this method may print errors, if there
   *                 is no method symbol of the given name at all, regardless of matching argument types.
   */
  protected void calculateQualifiedMethod(String qualName,
                                          ASTCallExpression callExpr,
                                          List<SymTypeExpression> argTypes) {
    List<FunctionSymbol> funcSymbols = getScope(callExpr.getEnclosingScope()).resolveFunctionMany(qualName);
    List<FunctionSymbol> methodList = new ArrayList<>(funcSymbols);

    calculateMethodReturnTypeBasedOnSignature(methodList, callExpr, argTypes);
  }

  /**
   * Checks whether any of the {@code candidates} methods that the {@code callExpr} may represent match the given
   * {@code argTypes} signature. If there are multiple candidates remaining and the callExpr has bounds on the types
   * that it may represent, then the candidate with the matching return type is chosen.
   * The chosen candidate's return type is then set as the current type check result.
   * @param argTypes The types of the method arguments. Are allowed to be obscure, but in this case the method will also
   *                 always set *obscure* to be the method's type. Nevertheless, this method may print errors, if there
   *                 is no method symbol of the given name at all, regardless of matching argument types.
   */
  protected void calculateMethodReturnTypeBasedOnSignature(List<FunctionSymbol> candidates,
                                                           ASTCallExpression callExpr,
                                                           List<SymTypeExpression> argTypes) {
    if(candidates.isEmpty()) {
      logError("0xA1242", callExpr.get_SourcePositionStart());
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }

    // From now on we need the argument types of the method call. Hence, if the arguments are malformed we can not
    // proceed and exit early
    if(!checkNotObscure(argTypes)) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }

    if(!checkNotObscure(argTypes)) {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }

    // Filter based on a compatible signature
    List<FunctionSymbol> fittingMethods = getFittingMethods(candidates, callExpr, argTypes);
    // There can only be one method with the correct arguments and return type
    if (!fittingMethods.isEmpty()) {
      if (fittingMethods.size() > 1) {
        checkForReturnType(callExpr, fittingMethods);
      }
      callExpr.setDefiningSymbol(fittingMethods.get(0));
      SymTypeExpression wholeResult = fittingMethods.get(0).getType();
      getTypeCheckResult().setMethod();
      getTypeCheckResult().setResult(wholeResult);
    } else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA1241", callExpr.get_SourcePositionStart());
    }
  }

  protected List<SymTypeExpression> calculateArguments(ASTCallExpression expr, String methodName){
    List<SymTypeExpression> returnList = new ArrayList<>();
    for(int i = 0; i < expr.getArguments().sizeExpressions(); i++){
      getTypeCheckResult().reset();
      expr.getArguments().getExpression(i).accept(getTraverser());
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().isType()){
        returnList.add(getTypeCheckResult().getResult());
      }else{
        //Placeholder as no function can have a parameter of type void and so that the correct number of
        //SymTypeExpressions is in the list
        returnList.add(SymTypeExpressionFactory.createObscureType());
      }
    }
    return returnList;
  }

  protected void checkForReturnType(ASTCallExpression expr, List<FunctionSymbol> fittingMethods){
    SymTypeExpression returnType = fittingMethods.get(0).getType();
    for (FunctionSymbol method : fittingMethods) {
      if (!returnType.deepEquals(method.getType())) {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1239", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct functions/methods from a type based on their modifiers
   */
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr, String name) {
    return innerResult.getMethodList(name, getTypeCheckResult().isType(), true);
  }

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr, List<SymTypeExpression> args) {
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if ((!method.isIsElliptic() &&
          args.size() == method.getParameterList().size())
          || (method.isIsElliptic() &&
          args.size() >= method.getParameterList().size() - 1)) {
        boolean success = true;
        for (int i = 0; i < args.size(); i++) {
          //test if every single argument is correct
          //if an argument is void type then it could not be calculated correctly -> see calculateArguments
          SymTypeExpression paramType = method.getParameterList().get(Math.min(i, method.getParameterList().size() - 1)).getType();
          if (!paramType.deepEquals(args.get(i)) &&
            !compatible(paramType, args.get(i)) || args.get(i).isVoidType()) {
            success = false;
          }
        }
        if (success) {
          //method has the correct arguments and return type
          fittingMethods.add(method);
        }
      }
    }
    return fittingMethods;
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols;
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  protected SymTypeExpression calculateTypeCompare(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (isNumericType(left) && isNumericType(right)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    } else {
      // else operator not applicable
      Log.error("0xA0167 Operator '" + op + "' not applicable to " + "'" + left.print() + "', '" + right.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * helper method for ==, != calculates the result of these expressions
   */
  protected SymTypeExpression calculateTypeLogical(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
    //Option one: they are both numeric types
    if (isNumericType(left) && isNumericType(right) || isBoolean(left) && isBoolean(right)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    } else if (compatible(left, right) || compatible(right, left)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    } else {
      // else operator not applicable
      Log.error("0xA0166 Operator '" + op + "' not applicable to " + "'" + left.print() + "', '" + right.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected SymTypeExpression getBinaryNumericPromotion(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if ((isDouble(leftResult) && isNumericType(rightResult)) ||
        (isDouble(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("double");
      //no part of the expression is a double -> try again with float
    } else if ((isFloat(leftResult) && isNumericType(rightResult)) ||
        (isFloat(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("float");
      //no part of the expression is a float -> try again with long
    } else if ((isLong(leftResult) && isNumericType(rightResult)) ||
        (isLong(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("long");
      //no part of the expression is a long -> if both parts are numeric types then the result is a int
    } else if (isIntegralType(leftResult) && isIntegralType(rightResult)
    ) {
      return SymTypeExpressionFactory.createPrimitive("int");
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  protected SymTypeExpression calculateArithmeticExpression(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
    // if one part of the expression is a double and the other is another numeric type then the result is a double
    if ((isDouble(left) && isNumericType(right)) || (isDouble(right) && isNumericType(left))) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
      // no part of the expression is a double -> try again with float
    } else if ((isFloat(left) && isNumericType(right)) || (isFloat(right) && isNumericType(left))) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
      // no part of the expression is a float -> try again with long
    } else if ((isLong(left) && isNumericType(right)) || (isLong(right) && isNumericType(left))) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
      // no part of the expression is a long -> if both parts are numeric types then the result is an int
    } else if (isIntegralType(left) && isIntegralType(right)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    } else {
      // else operator not applicable
      Log.error("0xA0168 Operator '" + op + "' not applicable to " + "'" + left.print() + "', '" + right.print() + "'", pos);
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }
}
