// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class ExpressionCompatibilityConstraint extends Constraint {

  protected ASTExpression expr;
  protected SymTypeExpression targetType;

  public ExpressionCompatibilityConstraint(
      ASTExpression expr,
      SymTypeExpression targetType
  ) {
    this.expr = Log.errorIfNull(expr);
    this.targetType = Log.errorIfNull(targetType);
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
    return "<Expression --> " + targetType.printFullName() + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getTargetType());
  }
}
