/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypePrimitive.unbox;
import static de.monticore.types.check.TypeCheck.compatible;
import static de.monticore.types.check.TypeCheck.isBoolean;
import static de.monticore.types.check.TypeCheck.isString;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in AssignmentExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfAssignmentExpressions extends AbstractDeriveFromExpression
  implements AssignmentExpressionsVisitor2, AssignmentExpressionsHandler {

  protected AssignmentExpressionsTraverser traverser;

  @Override
  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void traverse(ASTIncSuffixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.affix(expr.getExpression(), "++", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);

  }

  @Override
  public void traverse(ASTDecSuffixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.affix(expr.getExpression(), "--", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.affix(expr.getExpression(), "++", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.affix(expr.getExpression(), "--", expr.get_SourcePositionStart());

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression affix(ASTExpression expr, String op, SourcePosition pos) {
    // calculate the type of the inner expressions
    this.getTypeCheckResult().reset();
    expr.accept(this.getTraverser());
    TypeCheckResult inner = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!inner.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0182", expr.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (inner.getResult().isObscureType()) {
      // if inner obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else if (!inner.isField()) {
      // inner should be a field
      Log.error("0xA0183 Variable expected.", expr.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else {
      // else check with signature
      return affix(inner.getResult(), op, pos);
    }
  }

  protected SymTypeExpression affix(SymTypeExpression inner, String op, SourcePosition pos) {
    if (isNumericType(inner)) {
      return SymTypeExpressionFactory.createPrimitive(unbox(inner.print()));
    } else {
      Log.error("0xA0184 Operator '" + op + "' not applicable to " + "'" + inner.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }


  @Override
  public void traverse(ASTAssignmentExpression expr) {
    Preconditions.checkNotNull(expr);
    SymTypeExpression symType = this.derive(expr);

    this.getTypeCheckResult().reset();
    this.getTypeCheckResult().setResult(symType);
  }

  protected SymTypeExpression derive(ASTAssignmentExpression expr) {
    // calculate the type of inner expressions
    this.getTypeCheckResult().reset();
    expr.getLeft().accept(this.getTraverser());
    TypeCheckResult left = this.getTypeCheckResult().copy();
    this.getTypeCheckResult().reset();
    expr.getRight().accept(this.getTraverser());
    TypeCheckResult right = this.getTypeCheckResult().copy();

    // result of inner type computation should be present
    if (!left.isPresentResult() || !right.isPresentResult()) {
      // should never happen, we expect results to be present
      // indicates that the underlying type resolver is erroneous
      this.logError("0xA0180", expr.get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else if (left.getResult().isObscureType() || right.getResult().isObscureType()) {
      // if left or right obscure then error already logged
      return SymTypeExpressionFactory.createObscureType();
    } else if (!left.isField()) {
      // left should be a field
      Log.error("0xA0181 Variable expected.", expr.getLeft().get_SourcePositionStart());
      return SymTypeExpressionFactory.createObscureType();
    } else {
      // else compare the inner results
      return assignment(left.getResult(), right.getResult(), expr.getOperator(), expr.get_SourcePositionStart());
    }
  }


  protected SymTypeExpression assignment(SymTypeExpression left,
                                         SymTypeExpression right,
                                         int op, SourcePosition pos) {
    if (op == ASTConstantsAssignmentExpressions.PLUSEQUALS) {
      return this.addAssignment(left, right, pos); // a += b
    } else if (op == ASTConstantsAssignmentExpressions.MINUSEQUALS) {
      return this.subtractAssignment(left, right, pos); // a -= b
    } else if (op == ASTConstantsAssignmentExpressions.STAREQUALS) {
      return this.multiplyAssignment(left, right, pos); // a *= b
    } else if (op == ASTConstantsAssignmentExpressions.SLASHEQUALS) {
      return this.divideAssignment(left, right, pos); // a /= b
    } else if (op == ASTConstantsAssignmentExpressions.PERCENTEQUALS) {
      return this.moduloAssignment(left, right, pos); // a %= b
    } else if (op == ASTConstantsAssignmentExpressions.AND_EQUALS) {
      return this.andAssignment(left, right, pos); // a %= b
    } else if (op == ASTConstantsAssignmentExpressions.PIPEEQUALS) {
      return this.orAssignment(left, right, pos); // a |= b
    } else if (op == ASTConstantsAssignmentExpressions.ROOFEQUALS) {
      return this.xorAssignment(left, right, pos); // a ^= b
    } else if (op == ASTConstantsAssignmentExpressions.GTGTEQUALS) {
      return this.rightShiftAssignment(left, right, pos); // a >>= b
    } else if (op == ASTConstantsAssignmentExpressions.LTLTEQUALS) {
      return this.leftShiftAssignment(left, right, pos); // a <<= b
    } else if (op == ASTConstantsAssignmentExpressions.GTGTGTEQUALS) {
      return this.unsignedRightShiftAssignment(left, right, pos); // a >>>= b
    } else {
      return this.assignment(left, right, pos); // a = b
    }
  }

  protected SymTypeExpression addAssignment(SymTypeExpression left,
                                            SymTypeExpression right,
                                            SourcePosition pos) {
    // anything on the rhs be converted to a String
    if (isString(left)) {
      return left;
    } else {
      return arithmeticAssignment(left, right, "+=", pos);
    }
  }

  protected SymTypeExpression subtractAssignment(SymTypeExpression left,
                                                 SymTypeExpression right,
                                                 SourcePosition pos) {
    return arithmeticAssignment(left, right, "-=", pos);
  }

  protected SymTypeExpression multiplyAssignment(SymTypeExpression left,
                                                 SymTypeExpression right,
                                                 SourcePosition pos) {
    return arithmeticAssignment(left, right, "*=", pos);
  }

  protected SymTypeExpression moduloAssignment(SymTypeExpression left,
                                               SymTypeExpression right,
                                               SourcePosition pos) {
    return arithmeticAssignment(left, right, "%=", pos);
  }

  protected SymTypeExpression divideAssignment(SymTypeExpression left,
                                               SymTypeExpression right,
                                               SourcePosition pos) {
    return arithmeticAssignment(left, right, "/=", pos);
  }

  protected SymTypeExpression andAssignment(SymTypeExpression left,
                                            SymTypeExpression right,
                                            SourcePosition pos) {
    return binaryAssignment(left, right, "&=", pos);
  }

  protected SymTypeExpression orAssignment(SymTypeExpression left,
                                           SymTypeExpression right,
                                           SourcePosition pos) {
    return binaryAssignment(left, right, "|=", pos);
  }

  protected SymTypeExpression xorAssignment(SymTypeExpression left,
                                            SymTypeExpression right,
                                            SourcePosition pos) {
    return binaryAssignment(left, right, "^=", pos);
  }


  protected SymTypeExpression rightShiftAssignment(SymTypeExpression left,
                                                   SymTypeExpression right,
                                                   SourcePosition pos) {
    return bitAssignment(left, right, ">>=", pos);
  }

  protected SymTypeExpression leftShiftAssignment(SymTypeExpression left,
                                                  SymTypeExpression right,
                                                  SourcePosition pos) {
    return bitAssignment(left, right, "<<=", pos);
  }

  protected SymTypeExpression unsignedRightShiftAssignment(SymTypeExpression left,
                                                           SymTypeExpression right,
                                                           SourcePosition pos) {
    return bitAssignment(left, right, ">>>=", pos);
  }

  protected SymTypeExpression binaryAssignment(SymTypeExpression left,
                                               SymTypeExpression right,
                                               String op, SourcePosition pos) {
    // both must be of integral or both must be of boolean type
    if ((isIntegralType(left) && isIntegralType(right))
      || (isBoolean(left) && isBoolean(right))) {
      return left;
    } else {
      // else operator not applicable
      Log.error("0xA0176 Operator '" + op + "' not applicable to "
        + "'" + left.print() + "', '" + right.print() + "'", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  protected SymTypeExpression bitAssignment(SymTypeExpression left,
                                            SymTypeExpression right,
                                            String op,
                                            SourcePosition src) {
    // both must be of integral type
    if (isIntegralType(left) && isIntegralType(right)) {
      return left;
    } else {
      // else operator not applicable
      Log.error("0xA0177 Operator '" + op + "' not applicable to "
        + "'" + left.print() + "', '" + right.print() + "'", src);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  protected SymTypeExpression arithmeticAssignment(SymTypeExpression left,
                                                   SymTypeExpression right,
                                                   String op,
                                                   SourcePosition src) {
    // both must be of numeric type
    if (isNumericType(left) && isNumericType(right)) {
      return left;
    } else {
      // else operator not applicable
      Log.error("0xA0178 Operator '" + op + "' not applicable to "
        + "'" + left.print() + "', '" + right.print() + "'", src);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  protected SymTypeExpression assignment(SymTypeExpression left,
                                         SymTypeExpression right,
                                         SourcePosition src) {
    // types must be compatible
    if (compatible(left, right)) {
      return left;
    } else {
      // else type mismatch
      Log.error("0xA0179 Incompatible types, required '" + left.print()
        + "' but provided '" + right.print() + "'", src);
      return SymTypeExpressionFactory.createObscureType();
    }
  }
}
