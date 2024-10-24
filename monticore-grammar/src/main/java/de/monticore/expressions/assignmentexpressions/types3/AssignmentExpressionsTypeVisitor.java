/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.types3;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class AssignmentExpressionsTypeVisitor extends AbstractTypeVisitor
    implements AssignmentExpressionsVisitor2 {

  // due to legacy reasons, error codes are identical for different operators,
  // even if they have different implementations,
  // e.g., '+=' and '*=' support different SIUnits.
  protected static final String ARITHMETIC_ASSIGNMENT_ERROR_CODE = "0xA0178";
  protected static final String BIT_ASSIGNMENT_ERROR_CODE = "0xA0177";
  protected static final String BINARY_ASSIGNMENT_ERROR_CODE = "0xA0176";

  protected TypeVisitorOperatorCalculator operatorCalculator
      = new TypeVisitorOperatorCalculator();

  public void setOperatorCalculator(
      TypeVisitorOperatorCalculator operatorCalculator) {
    this.operatorCalculator = operatorCalculator;
  }

  protected TypeVisitorOperatorCalculator getOperatorCalculator() {
    return operatorCalculator;
  }

  // Note: there is currently no SIUnit support for
  // increment/decrement prefix/postfix
  // this is deliberate, but may be added in the future if required.

  @Override
  public void endVisit(ASTIncSuffixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((innerPar) -> this.affix(innerPar, "++",
            expr.get_SourcePositionStart()))
        .apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTDecSuffixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((innerPar) -> this.affix(innerPar, "--",
            expr.get_SourcePositionStart()))
        .apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTIncPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((innerPar) -> this.affix(innerPar, "++",
            expr.get_SourcePositionStart()))
        .apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Override
  public void endVisit(ASTDecPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression inner = getType4Ast().getPartialTypeOfExpr(expr.getExpression());
    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((innerPar) -> this.affix(innerPar, "--",
            expr.get_SourcePositionStart()))
        .apply(inner);
    getType4Ast().setTypeOfExpression(expr, result);
  }

  protected SymTypeExpression affix(SymTypeExpression inner, String op, SourcePosition pos) {
    if (SymTypeRelations.isNumericType(inner)) {
      SymTypeExpression unboxed = SymTypeRelations.unbox(inner);
      return SymTypeExpressionFactory.createPrimitive(unboxed.print());
    }
    else {
      Log.error("0xA0184 Operator '" + op + "' not applicable to " + "'" + inner.print() + "'",
          pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  @Override
  public void endVisit(ASTAssignmentExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression left = getType4Ast().getPartialTypeOfExpr(expr.getLeft());
    SymTypeExpression right = getType4Ast().getPartialTypeOfExpr(expr.getRight());
    SymTypeExpression result = assignment(left, right, expr.getOperator(), expr.get_SourcePositionStart());

    getType4Ast().setTypeOfExpression(expr, result);
  }

  @Deprecated(forRemoval = true)
  protected SymTypeExpression derive(
      ASTAssignmentExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return assignment(left, right, expr.getOperator(), expr.get_SourcePositionStart());

  }

  protected SymTypeExpression assignment(SymTypeExpression left,
      SymTypeExpression right,
      int op, SourcePosition pos) {
    if (op == ASTConstantsAssignmentExpressions.PLUSEQUALS) {
      return this.addAssignment(left, right, pos); // a += b
    }
    else if (op == ASTConstantsAssignmentExpressions.MINUSEQUALS) {
      return this.subtractAssignment(left, right, pos); // a -= b
    }
    else if (op == ASTConstantsAssignmentExpressions.STAREQUALS) {
      return this.multiplyAssignment(left, right, pos); // a *= b
    }
    else if (op == ASTConstantsAssignmentExpressions.SLASHEQUALS) {
      return this.divideAssignment(left, right, pos); // a /= b
    }
    else if (op == ASTConstantsAssignmentExpressions.PERCENTEQUALS) {
      return this.moduloAssignment(left, right, pos); // a %= b
    }
    else if (op == ASTConstantsAssignmentExpressions.AND_EQUALS) {
      return this.andAssignment(left, right, pos); // a %= b
    }
    else if (op == ASTConstantsAssignmentExpressions.PIPEEQUALS) {
      return this.orAssignment(left, right, pos); // a |= b
    }
    else if (op == ASTConstantsAssignmentExpressions.ROOFEQUALS) {
      return this.xorAssignment(left, right, pos); // a ^= b
    }
    else if (op == ASTConstantsAssignmentExpressions.GTGTEQUALS) {
      return this.rightShiftAssignment(left, right, pos); // a >>= b
    }
    else if (op == ASTConstantsAssignmentExpressions.LTLTEQUALS) {
      return this.leftShiftAssignment(left, right, pos); // a <<= b
    }
    else if (op == ASTConstantsAssignmentExpressions.GTGTGTEQUALS) {
      return this.unsignedRightShiftAssignment(left, right, pos); // a >>>= b
    }
    else {
      return this.assignment(left, right, pos); // a = b
    }
  }

  protected SymTypeExpression addAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().plus(left, right),
        ARITHMETIC_ASSIGNMENT_ERROR_CODE, "+=", pos);
  }

  protected SymTypeExpression subtractAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().minus(left, right),
        ARITHMETIC_ASSIGNMENT_ERROR_CODE, "-=", pos);
  }

  protected SymTypeExpression multiplyAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().multiply(left, right),
        ARITHMETIC_ASSIGNMENT_ERROR_CODE, "*=", pos);
  }

  protected SymTypeExpression moduloAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().modulo(left, right),
        ARITHMETIC_ASSIGNMENT_ERROR_CODE, "%=", pos);
  }

  protected SymTypeExpression divideAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().divide(left, right),
        ARITHMETIC_ASSIGNMENT_ERROR_CODE, "/=", pos);
  }

  protected SymTypeExpression andAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().binaryAnd(left, right),
        BINARY_ASSIGNMENT_ERROR_CODE, "&=", pos);
  }

  protected SymTypeExpression orAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().binaryOr(left, right),
        BINARY_ASSIGNMENT_ERROR_CODE, "|=", pos);
  }

  protected SymTypeExpression xorAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().binaryXor(left, right),
        BINARY_ASSIGNMENT_ERROR_CODE, "^=", pos);
  }

  protected SymTypeExpression rightShiftAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().signedRightShift(left, right),
        BIT_ASSIGNMENT_ERROR_CODE, ">>=", pos);
  }

  protected SymTypeExpression leftShiftAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().leftShift(left, right),
        BIT_ASSIGNMENT_ERROR_CODE, "<<=", pos);
  }

  protected SymTypeExpression unsignedRightShiftAssignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition pos) {
    return compoundAssignment(left, right,
        getOperatorCalculator().unsignedRightShift(left, right),
        BIT_ASSIGNMENT_ERROR_CODE, ">>>=", pos);
  }

  /**
   * JLS 20, 15.26.2: A compound assignment expression of the form E1 op= E2
   * is equivalent to E1 = (T) ((E1) op (E2)), where T is the type of E1,
   * except that E1 is evaluated only once.
   *
   * @param interimResult type of ((E1) op (E2))
   */
  protected SymTypeExpression compoundAssignment(
      SymTypeExpression left, SymTypeExpression right,
      Optional<SymTypeExpression> interimResult,
      String errorCode, String op, SourcePosition pos
  ) {
    Optional<SymTypeExpression> casted = interimResult
        .flatMap(ir -> getOperatorCalculator().cast(left, ir));
    Optional<SymTypeExpression> assigned = casted
        .flatMap(c -> getOperatorCalculator().assignment(left, c));
    SymTypeExpression result = getTypeForAssignmentOrLogError(
        errorCode, op, pos, assigned, left, right);
    return result;
  }

  protected SymTypeExpression assignment(SymTypeExpression left,
      SymTypeExpression right,
      SourcePosition src) {
    return getTypeForAssignmentOrLogError(
        "0xA0179", "=", src,
        getOperatorCalculator().assignment(left, right),
        left, right);
  }

  // Helper

  protected SymTypeExpression getTypeForAssignmentOrLogError(
      String errorCode, String op, SourcePosition pos,
      Optional<SymTypeExpression> result,
      SymTypeExpression left, SymTypeExpression right
  ) {
    if (left.isObscureType() || right.isObscureType()) {
      return createObscureType();
    }
    if (result.isPresent()) {
      return result.get();
    }
    else {
      // operator not applicable
      Log.error(errorCode
              + " Operator '" + op + "' not applicable to " +
              "'" + left.print() + "', '"
              + right.print() + "'",
          pos
      );
      return createObscureType();
    }
  }

}
