package de.monticore.expressions.bitexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class BitExpressionsTypeVisitor extends AbstractTypeVisitor
    implements BitExpressionsVisitor2 {

  // due to legacy reasons, error codes are identical for different operators
  protected static final String SHIFT_OPERATOR_ERROR_CODE = "0xC0201";
  protected static final String BINARY_OPERATOR_ERROR_CODE = "0xC0203";

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
  public void endVisit(ASTLeftShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        SHIFT_OPERATOR_ERROR_CODE, expr, expr.getShiftOp(),
        getOperatorCalculator().leftShift(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        SHIFT_OPERATOR_ERROR_CODE, expr, expr.getShiftOp(),
        getOperatorCalculator().signedRightShift(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTLogicalRightShiftExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        SHIFT_OPERATOR_ERROR_CODE, expr, expr.getShiftOp(),
        getOperatorCalculator().unsignedRightShift(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBinaryAndExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        BINARY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().binaryAnd(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBinaryOrOpExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        BINARY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().binaryOr(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTBinaryXorExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = getTypeForOperatorOrLogError(
        BINARY_OPERATOR_ERROR_CODE, expr, expr.getOperator(),
        getOperatorCalculator().binaryXor(left, right), left, right
    );
    getType4Ast().setTypeOfExpression(expr, result);
  }

  // Helper

  protected SymTypeExpression getTypeForOperatorOrLogError(
      String errorCode, ASTExpression expr, String op,
      Optional<SymTypeExpression> result,
      SymTypeExpression left, SymTypeExpression right
  ) {
    if (left.isObscureType() && right.isObscureType()) {
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
