/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

/**
 * Abstraction over the pair of an expression that is linked to a name.
 */
public final class ExprToNamePair {
  private final ASTExpression expression;
  private final String name;
  private ExprToNamePair(ASTExpression expression, String name) {
    this.expression = expression;
    this.name = name;
  }

  public static ExprToNamePair of(ASTExpression expression, String name) {
    return new ExprToNamePair(expression, name);
  }

  public ASTExpression getExpression() {
    return expression;
  }

  public String getName() {
    return name;
  }
}
