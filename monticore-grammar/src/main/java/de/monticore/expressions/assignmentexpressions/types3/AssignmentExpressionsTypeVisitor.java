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
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

public class AssignmentExpressionsTypeVisitor extends AbstractTypeVisitor
    implements AssignmentExpressionsVisitor2 {

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

    SymTypeExpression result = TypeVisitorLifting
        .liftDefault((leftPar, rightPar) -> derive(expr, leftPar, rightPar))
        .apply(left, right);
    getType4Ast().setTypeOfExpression(expr, result);
  }

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
    // anything on the rhs be converted to a String
    if (SymTypeRelations.isString(left)) {
      return left;
    }
    else {
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
    if ((SymTypeRelations.isIntegralType(left) && SymTypeRelations.isIntegralType(right))
        || (SymTypeRelations.isBoolean(left) && SymTypeRelations.isBoolean(right))) {
      return left;
    }
    else {
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
    if (SymTypeRelations.isIntegralType(left) && SymTypeRelations.isIntegralType(right)) {
      return left;
    }
    else {
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
    if (SymTypeRelations.isNumericType(left) && SymTypeRelations.isNumericType(right)) {
      return left;
    }
    else {
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
    if (SymTypeRelations.isCompatible(left, right)) {
      return left;
    }
    else {
      // else type mismatch
      Log.error("0xA0179 Incompatible types, required '" + left.print()
          + "' but provided '" + right.print() + "'", src);
      return SymTypeExpressionFactory.createObscureType();
    }
  }
}
