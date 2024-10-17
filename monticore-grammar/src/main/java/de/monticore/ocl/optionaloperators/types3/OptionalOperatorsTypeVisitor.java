package de.monticore.ocl.optionaloperators.types3;

import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalExpressionPrefix;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalGreaterThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessEqualExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalLessThanExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotEqualsExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotSimilarExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalSimilarExpression;
import de.monticore.ocl.optionaloperators._visitor.OptionalOperatorsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

public class OptionalOperatorsTypeVisitor extends AbstractTypeVisitor
    implements OptionalOperatorsVisitor2 {

  // due to legacy reasons, error codes are identical for different operators
  protected static final String OPT_NUMERIC_COMPARISON_ERROR_CODE = "0xFD280";
  protected static final String OPT_NUMERIC_EXPECTED_ERROR_CODE = "0xFD209";
  protected static final String OPT_EQUALITY_ERROR_CODE = "0xFD285";

  protected TypeVisitorOperatorCalculator operatorCalculator
      = new TypeVisitorOperatorCalculator();

  public void setOperatorCalculator(
      TypeVisitorOperatorCalculator operatorCalculator) {
    this.operatorCalculator = operatorCalculator;
  }

  protected TypeVisitorOperatorCalculator getOperatorCalculator() {
    return operatorCalculator;
  }

  @Override
  public void endVisit(ASTOptionalExpressionPrefix expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) ->
                    calculateOptionalExpressionPrefix(
                        expr.getLeft(), expr.getRight(), leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateOptionalExpressionPrefix(
      ASTExpression left,
      ASTExpression right,
      SymTypeExpression leftResult,
      SymTypeExpression rightResult) {
    if (!MCCollectionSymTypeRelations.isOptional(leftResult)) {
      Log.error(
          "0xFDB74 expected Optional at '?:' but got "
              + leftResult.printFullName(),
          left.get_SourcePositionStart(),
          left.get_SourcePositionEnd());
      return createObscureType();
    }
    // check compatibility of type of optional and expression
    else {
      SymTypeExpression elementType =
          MCCollectionSymTypeRelations.getCollectionElementType(leftResult);
      return createUnion(elementType, rightResult);
    }
  }

  @Override
  public void endVisit(ASTOptionalLessEqualExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftNum = getNumericTypeOfOptionalOrLogError(expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().lessEqual(leftNum, right), leftNum, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalGreaterEqualExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftNum = getNumericTypeOfOptionalOrLogError(expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().greaterEqual(leftNum, right), leftNum, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalLessThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftNum = getNumericTypeOfOptionalOrLogError(expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().lessThan(leftNum, right), leftNum, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalGreaterThanExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftNum = getNumericTypeOfOptionalOrLogError(expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_NUMERIC_COMPARISON_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().greaterThan(leftNum, right), leftNum, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalEqualsExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftElem = getTypeOfOptionalOrLogError(
        OPT_EQUALITY_ERROR_CODE, expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_EQUALITY_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().equality(leftElem, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalNotEqualsExpression expr) {
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression leftElem = getTypeOfOptionalOrLogError(
        OPT_EQUALITY_ERROR_CODE, expr.getLeft(), left);
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForInfixOrLogError(
        OPT_EQUALITY_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().inequality(leftElem, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalSimilarExpression expr) {
    // no compatibility check necessary, therefore only check for optional
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());

    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) ->
                    calculateOptionalSimilarityExpression(expr, leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTOptionalNotSimilarExpression expr) {
    // no compatibility check necessary, therefore only check for optional
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result =
        TypeVisitorLifting.liftDefault(
                (leftPar, rightPar) ->
                    calculateOptionalSimilarityExpression(expr, leftPar, rightPar))
            .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression calculateOptionalSimilarityExpression(
      ASTExpression expr, SymTypeExpression leftType, SymTypeExpression rightType) {
    SymTypeExpression result;

    if (!MCCollectionSymTypeRelations.isOptional(leftType)) {
      Log.error(
          "0xFD203 expected Optional but got " + leftType.printFullName(),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd());
      result = createObscureType();
    }
    else {
      result = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return result;
  }

  // Helper

  protected SymTypeExpression getTypeOfOptionalOrLogError(
      String errorCode, ASTExpression optNode, SymTypeExpression optType
  ) {
    SymTypeExpression result;
    if (optType.isObscureType()) {
      result = createObscureType();
    }
    else if (MCCollectionSymTypeRelations.isOptional(optType)) {
      result = MCCollectionSymTypeRelations.getCollectionElementType(optType);
    }
    else {
      Log.error(errorCode + " expected Optional but got "
              + optType.printFullName(),
          optNode.get_SourcePositionStart(),
          optNode.get_SourcePositionEnd());
      result = createObscureType();
    }
    return result;
  }

  protected SymTypeExpression getNumericTypeOfOptionalOrLogError(
      ASTExpression optOfNumNode,
      SymTypeExpression optOfNumType
  ) {
    SymTypeExpression result;
    SymTypeExpression numType = getTypeOfOptionalOrLogError(
        OPT_NUMERIC_EXPECTED_ERROR_CODE, optOfNumNode, optOfNumType);
    if (optOfNumType.isObscureType()) {
      result = createObscureType();
    }
    if (MCCollectionSymTypeRelations.isNumericType(numType)) {
      return numType;
    }
    Log.error(
        "0xFD209 expected Optional of a numeric type, but got "
            + optOfNumType.printFullName(),
        optOfNumNode.get_SourcePositionStart(),
        optOfNumNode.get_SourcePositionEnd());
    return createObscureType();
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
}
