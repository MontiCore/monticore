/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;

import java.util.Optional;

/**
 * A common interface that can be used to derive SymTypeExpressions from expressions and literals
 */
public interface IDerive {

  /**
   * Collects the derived SymTypeExpressions after
   * using the traverser to traverse the expression or literal
   */
  Optional<SymTypeExpression> getResult();

  /**
   * Initializes the traverser with the correct visitors and handlers
   */
  void init();

  ExpressionsBasisTraverser getTraverser();
}
