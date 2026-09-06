// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import java.util.List;

public class ExpressionCompatibilityConstraint extends Constraint {

  protected ASTExpression expr;
  protected SymTypeExpression targetType;

  public ExpressionCompatibilityConstraint(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    this.expr = Preconditions.checkNotNull(expr);
    this.targetType = Preconditions.checkNotNull(targetType);
  }

  public ASTExpression getExpr() {
    return expr;
  }

  public SymTypeExpression getTargetType() {
    return targetType;
  }

  @Override
  public boolean isExpressionCompatibilityConstraint() {
    return true;
  }

  @Override
  public ExpressionCompatibilityConstraint asExpressionCompatibilityConstraint() {
    return this;
  }

  @Override
  public boolean deepEquals(Constraint other) {
    if (this == other) {
      return true;
    }
    if (!other.isExpressionCompatibilityConstraint()) {
      return false;
    }
    ExpressionCompatibilityConstraint otherExprComp = (ExpressionCompatibilityConstraint) other;
    return getExpr().deepEquals(otherExprComp.getExpr()) &&
        getTargetType().deepEquals(otherExprComp.getTargetType());
  }

  @Override
  public String print() {
    return "<Expression["
        + expr.get_SourcePositionStart() + "-" + expr.get_SourcePositionEnd()
        + "] --> " + targetType.printFullName() + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getTargetType());
  }
}
