package de.monticore.expressions.streamexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.List;

public interface ASTStreamConstructorExpression
    extends ASTStreamConstructorExpressionTOP {

  /**
   * If default and not specified otherwise, Event should be chosen.
   */
  default boolean isDefaultTimed() {
    return false;
  }

  default boolean isEventTimed() {
    return false;
  }

  default boolean isSyncTimed() {
    return false;
  }

  default boolean isToptTimed() {
    return false;
  }

  default boolean isUntimed() {
    return false;
  }

  /**
   * @return all ASTExpressions directly found in the constructor.
   */
  List<ASTExpression> getExpressionList();

}
