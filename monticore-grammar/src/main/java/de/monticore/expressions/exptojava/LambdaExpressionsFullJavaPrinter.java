/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions.LambdaExpressionsMill;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class LambdaExpressionsFullJavaPrinter extends ExpressionsBasisFullJavaPrinter {

  protected LambdaExpressionsTraverser traverser;

  @Override
  public LambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(LambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public LambdaExpressionsFullJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = LambdaExpressionsMill.traverser();

    ExpressionsBasisJavaPrinter expressionBasis = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionBasis);
    traverser.add4ExpressionsBasis(expressionBasis);
    LambdaExpressionsJavaPrinter javaClassExpression = new LambdaExpressionsJavaPrinter(printer);
    traverser.setLambdaExpressionsHandler(javaClassExpression);
    traverser.add4LambdaExpressions(javaClassExpression);
  }

  public String print(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

}
