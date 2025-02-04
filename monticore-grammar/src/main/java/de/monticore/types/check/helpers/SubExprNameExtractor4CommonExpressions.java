/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

/**
 * Provides a complete traverser for CommonExpressions and ExpressionBasis, using
 * {@link SubExprNameExtractionVisitor} as a visitor.
 */
public class SubExprNameExtractor4CommonExpressions implements SubExprNameExtractor {

  /**
   * Collects the name parts of the expression that the visitor is applied to.
   */
  protected SubExprNameExtractionResult subExpressions;

  protected final CommonExpressionsTraverser traverser;

  protected final SubExprNameExtractionVisitor nameCalculator;

  public SubExprNameExtractor4CommonExpressions() {
    this(CommonExpressionsMill.inheritanceTraverser());
  }

  public SubExprNameExtractor4CommonExpressions(CommonExpressionsTraverser traverser) {
    this.subExpressions = new SubExprNameExtractionResult();
    this.traverser = traverser;
    this.nameCalculator = new SubExprNameExtractionVisitor(subExpressions);
    init();
  }

  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  protected void init() {
    this.traverser.add4ExpressionsBasis(this.nameCalculator);
    this.traverser.add4CommonExpressions(this.nameCalculator);
  }

  @Override
  public SubExprNameExtractionResult calculateNameParts(ASTExpression expression) {
    this.subExpressions.reset();
    expression.accept(this.getTraverser());
    return subExpressions.copy();
  }
}
