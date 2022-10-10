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
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.*;

public class DeriveSymTypeOfBSCommonExpressions extends AbstractDeriveFromExpression implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
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
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.add(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression add(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    // if one part of the expression is a String then the whole expression is a String
    if(isString(left)) {
      return SymTypeExpressionFactory.createTypeObject(left.getTypeInfo());
    } else if (isString(right)) {
      return SymTypeExpressionFactory.createTypeObject(right.getTypeInfo());
    } else {
      // no String in the expression -> use the normal calculation for the basic arithmetic operators
      return arithmetic(left, right, "+", pos);
    }
  }

  protected SymTypeExpression arithmetic(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
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

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.multiply(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression multiply(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return arithmetic(right, left, "*", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.divide(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression divide(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return arithmetic(right, left, "/", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMinusExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.subtract(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression subtract(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return arithmetic(right, left, "-", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTModuloExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.modulo(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression modulo(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return arithmetic(right, left, "%", pos);
  }


  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

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
    return comparison(right, left, "<=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

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
    return comparison(right, left, ">=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

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
    return comparison(right, left, "<", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.greater(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression greater(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return comparison(right, left, ">", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.equals(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression equals(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return typeComparison(right, left, "==", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.notEquals(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression notEquals(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return typeComparison(right, left, "!=", pos);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.boolAnd(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression boolAnd(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return bool(left, right, "&&", pos);
  }

  @Override
  public void traverse(ASTBooleanOrOpExpression expr) {
    SymTypeExpression left = acceptAndReturnSymType(expr.getLeft());
    SymTypeExpression right = acceptAndReturnSymType(expr.getRight());

    if (left.isObscureType() || right.isObscureType()) {
      // if any inner obscure then error already logged
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    } else {
      // else calculate result
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(this.boolOr(left, right, expr.get_SourcePositionStart()));
    }
  }

  protected SymTypeExpression boolOr(SymTypeExpression left, SymTypeExpression right, SourcePosition pos) {
    return bool(left, right, "||", pos);
  }

  protected SymTypeExpression bool(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
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
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getCondition(), expr.getTrueExpression(), expr.getFalseExpression());
    if(checkNotObscure(innerTypes)){
      SymTypeExpression wholeResult = calculateConditionalExpressionType(innerTypes.get(0), innerTypes.get(1), innerTypes.get(2));
      storeResultOrLogError(wholeResult, expr, "0xA0234");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateConditionalExpressionType(SymTypeExpression conditionResult,
                                                                           SymTypeExpression trueResult,
                                                                           SymTypeExpression falseResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = trueResult;
      } else if (compatible(falseResult, trueResult)) {
        wholeResult = falseResult;
      } else {
        // first argument can be null since it should not be relevant to the type calculation
        wholeResult = getBinaryNumericPromotion(trueResult, falseResult);
      }
    }
    return wholeResult;
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
   * Checks whether the expression has the form of a valid qualified java name.
   */
  protected boolean isQualifiedName(ASTFieldAccessExpression expr) {
    ASTExpression currentExpr = expr;

    // Iterate over subexpressions to check whether they are of the form NameExpression ("." FieldAccessExpression)*
    // Therefore, NameExpression will terminate the traversal, indicating that the expression is a valid name.
    // If the pattern is broken by another expression, then the expression is no valid name, and we terminate.
    while(!(currentExpr instanceof ASTNameExpression)) {
      if(currentExpr instanceof ASTFieldAccessExpression) {
        currentExpr = ((ASTFieldAccessExpression) currentExpr).getExpression();
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether the expression has the form of a valid qualified, or unqualified, java name.
   */
  protected boolean isName(ASTExpression expr) {
    return expr instanceof ASTNameExpression ||
      (expr instanceof ASTFieldAccessExpression && isQualifiedName((ASTFieldAccessExpression) expr));
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    if(isQualifiedName(expr) && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      calculateNamingChainFieldAccess(expr);
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
  protected void calculateNamingChainFieldAccess(ASTFieldAccessExpression expr) {
    Optional<List<ASTExpression>> astNamePartsOpt = collectSubExpressions(expr);

    if (!astNamePartsOpt.isPresent()) {
      Log.error("0x0xA2310 (Internal error) The qualified name parts of a FieldAccessExpression can not be " +
        "calculated as the field access expression is no qualified name. " + expr.get_SourcePositionStart().toString());
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());

    } else {
      // We will incrementally try to build our result:
      // We will start with the first name part and check whether it resolves to an entity.
      // Then we will check whether further field accesses are (nested) accesses on that entity's members.
      // When there is no such entity, or it does not have a member we were looking for, then we try to resolve the
      // qualified name up to the part of the name where we currently are.
      List<ASTExpression> astNameParts = astNamePartsOpt.get();

      getTypeCheckResult().reset();
      for(int i = 0; i < astNameParts.size(); i++) {
        if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
          calculateFieldAccess((ASTFieldAccessExpression) astNameParts.get(i), true);
        } else {
          calculatedQualifiedEntity(astNameParts.subList(0, i + 1));
        }
      }

      if(!getTypeCheckResult().isPresentResult() || getTypeCheckResult().getResult().isObscureType()) {
        logError("0xA0241", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Calculate the type result of FieldAccessExpressions that do not represent qualified names.
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
   * Transforms {@link ASTNameExpression}s and {@link ASTFieldAccessExpression}s into the names they represent (for
   * field access expressions it takes the name of the accessed field/entity).
   */
  protected String astNameToString(ASTExpression expression) {
    if(expression instanceof ASTNameExpression) {
      return ((ASTNameExpression) expression).getName();
    } else if (expression instanceof ASTFieldAccessExpression) {
      return ((ASTFieldAccessExpression) expression).getName();
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * If the FieldAccessExpression represents a qualified name, then this method returns its name parts.
   * Else an empty optional is returned.
   */
  protected Optional<List<ASTExpression>> collectSubExpressions(ASTFieldAccessExpression expr) {
    ASTExpression currentExpr = expr;
    List<ASTExpression> nameParts = new LinkedList<>();

    while(!(currentExpr instanceof ASTNameExpression)) {
      if(currentExpr instanceof ASTFieldAccessExpression) {
        ASTFieldAccessExpression curExpr = (ASTFieldAccessExpression) currentExpr;
        nameParts.add(0, curExpr);

        currentExpr = curExpr.getExpression();  // Advance iteration
      } else {
        return Optional.empty();
      }
    }

    // Do not forget to add the terminal NameExpression
    nameParts.add(0, currentExpr);
    return Optional.of(nameParts);
  }

  /**
   * Tries to resolve the given name parts to a variable, type variable, or type and if a symbol is found, then
   * it(s type) is set as the current type check result.
   * If no symbol is found, then nothing happens (no error logged, no altering of the type check result).
   * If multiple fields are found, then the result is set to obscure, and an error is logged.
   * Variables take precedence over types variables that take precedence over
   * types.
   * @param astNameParts Expressions that represent a qualified identification of a {@link VariableSymbol},
   *                  {@link TypeVarSymbol}, or {@link TypeSymbol}. Therefore, the list that must contain a
   *                  {@code NameExpression} at the beginning, followed only by {@code FieldAccessExpression}s.
   */
  protected void calculatedQualifiedEntity(List<ASTExpression> astNameParts) {
    List<String> nameParts = astNameParts.stream().map(this::astNameToString).collect(Collectors.toList());
    String qualName = String.join(".", nameParts);
    ASTExpression lastExpr = astNameParts.get(astNameParts.size() - 1);

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
        trySetDefiningSymbolOrError(lastExpr, var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        getTypeCheckResult().setResult(type);
      }

    } else if (typeVarOpt.isPresent()) {
      TypeVarSymbol typeVar = typeVarOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeVariable(typeVar);
      trySetDefiningSymbolOrError(lastExpr, typeVar);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);

    } else if (typeSymbolOpt.isPresent()) {
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
      trySetDefiningSymbolOrError(lastExpr, typeSymbol);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);
    }
  }

  /**
   * If {@code expr} is of type {@link ASTNameExpression}, {@link ASTFieldAccessExpression}, or
   * {@link ASTCallExpression}, then {@code definingSymbol} is set as its defining symbol. Else, an error is logged.
   */
  protected void trySetDefiningSymbolOrError(ASTExpression expr, ISymbol definingSymbol) {
    if(expr instanceof ASTNameExpression) {
      ((ASTNameExpression) expr).setDefiningSymbol(definingSymbol);
    } else if(expr instanceof ASTFieldAccessExpression) {
      ((ASTFieldAccessExpression) expr).setDefiningSymbol(definingSymbol);
    } else if (expr instanceof ASTCallExpression) {
      ((ASTCallExpression) expr).setDefiningSymbol(definingSymbol);
    } else {
      Log.error("0xA2306 (Internal error) tried to set the symbol on an Expression that is none of the following:" +
        "ASTNameExpression, ASTFieldAccessExpression, ASTCallExpression.");
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    if (isName(expr.getExpression()) && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      calculateNamingChainCallExpression(expr);
    } else {
      calculateArithmeticCallExpression(expr);
    }
  }

  /**
   * Calculate the type result of call expressions that represent fully qualified method calls (like when calling a
   * static method: {@code pac.kage.Type.staticMethod()}) and methodCalls on cascading field accesses (e.g.,
   * {@code localField.innerField.instanceMethod()}). But not:
   * {@code pac.kage.Type.staticMethod().innerField.instanceMethod()}, as here <i>instanceMethod</i> is not only
   * qualified by names, but it is based on the access of a value returned by a CallExpression.
   * @param expr The only valid sub expressions of the CallExpression are FieldAccessExpressions, or a
   *             {@link ASTNameExpression} that is the leaf of the field access chain, or alternatively defines a local
   *             method call ({@code localMethod("foo")}.
   */
  protected void calculateNamingChainCallExpression(ASTCallExpression expr) {
    Optional<List<ASTExpression>> astNamePartsOpt = Optional.empty();

    if(expr.getExpression() instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression qualExpr = (ASTFieldAccessExpression) expr.getExpression();
      astNamePartsOpt = collectSubExpressions(qualExpr);
    } else if(expr.getExpression() instanceof ASTNameExpression){
      ASTNameExpression nameExpr = (ASTNameExpression) expr.getExpression();
      astNamePartsOpt = Optional.of(Collections.singletonList(nameExpr));
    }

    if(astNamePartsOpt.isEmpty()){
      Log.error("0x0xA2312 (Internal error) The (qualified) name parts of a CallExpression can not be " +
        "calculated as the call expression is not defined by a (qualified) name. "
        + expr.get_SourcePositionStart().toString());
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }

    List<ASTExpression> astNameParts = astNamePartsOpt.get();
    List<String> nameParts = astNameParts.stream().map(this::astNameToString).collect(Collectors.toList());
    List<SymTypeExpression> args = calculateArguments(expr, nameParts.get(nameParts.size() - 1));

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
        calculatedQualifiedEntity(astNameParts.subList(0, i + 1));
      }
    }

    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      calculateOwnedCallExpression(expr, args);
    } else {
      // Check whether we have a fully qualified method. Local method calls will also end in this program branch.
      String qualName = String.join(".", nameParts);
      calculateQualifiedMethod(qualName, expr, args);
    }
  }

  /**
   * Calculates the type result of the expression, given that it is not a simple, or qualified method access. E.g.,
   * this method calculates the result of {@code "FooBar".substring(0, 3)}, or
   * {@code ( foo ? new LinkedList<String>() : new ArrayList<String>() ).add("bar")}. On the other hand, this method is
   * not suited for {@code pac.kage.Owner.staticMethod()}, or {@code isInt()} (a local Method name).
   * Use {@link #calculateNamingChainCallExpression(ASTCallExpression)} in these cases.
   */
  protected void calculateArithmeticCallExpression(ASTCallExpression expr) {
    List<SymTypeExpression> args = calculateArguments(expr, astNameToString(expr.getExpression()));
    getTypeCheckResult().reset();
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());

    ASTExpression methodNameExpr = expr.getExpression();
    if (methodNameExpr instanceof ASTFieldAccessExpression) {
      // MethodName has the form `... .owner.methodName -> calc the type of the owner and then calculate its method type
      ((ASTFieldAccessExpression) methodNameExpr).getExpression().accept(getTraverser());
      calculateOwnedCallExpression(expr, args);
    } else {
      Log.debug("Unexpectedly, the call expression was not made up of a field access expression.",
        "DeriveSTOfBSCommonExpressions#calculateArithmeticCallExpression");
      calculateOwnedCallExpression(expr, args);
    }
  }

  /**
   * Calculates the type result of the call expression, given that the type result of the method owner has already been
   * computed (and is accessible via getTypeCheckResult()), and that the type of its arguments has already been
   * computed.
   */
  protected void calculateOwnedCallExpression(ASTCallExpression expr, List<SymTypeExpression> args) {
    if(!getTypeCheckResult().isPresentResult()
      || getTypeCheckResult().getResult().isObscureType()) {
      return;
    }

    String methodName = astNameToString(expr.getExpression());
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
  protected SymTypeExpression comparison(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
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
  protected SymTypeExpression typeComparison(SymTypeExpression left, SymTypeExpression right, String op, SourcePosition pos) {
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
}
