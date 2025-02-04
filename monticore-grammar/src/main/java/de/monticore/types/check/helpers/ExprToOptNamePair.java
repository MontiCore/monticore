/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.Optional;

/**
 * Abstraction over the pair of an expression that is optionally linked to a name.
 */
public final class ExprToOptNamePair {
  private final ASTExpression expression;
  private final Optional<String> name;
  private ExprToOptNamePair(ASTExpression expression, Optional<String> name) {
    this.expression = expression;
    this.name = name;
  }

  public static ExprToOptNamePair of(ASTExpression expression, Optional<String> name) {
    return new ExprToOptNamePair(expression, name);
  }

  public ASTExpression getExpression() {
    return expression;
  }

  public Optional<String> getName() {
    return name;
  }
}
