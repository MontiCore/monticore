/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions.LambdaExpressionsMill;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameters;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class LambdaExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  protected LambdaExpressionsTraverser traverser;

  @Override
  public LambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(LambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public LambdaExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = LambdaExpressionsMill.traverser();
    ExpressionsBasisPrettyPrinter expressionBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionBasis);
    traverser.add4ExpressionsBasis(expressionBasis);
    MCBasicTypesPrettyPrinter mcBasicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypes);
    traverser.add4MCBasicTypes(mcBasicTypes);
    LambdaExpressionsPrettyPrinter lambdaExpression = new LambdaExpressionsPrettyPrinter(printer);
    traverser.setLambdaExpressionsHandler(lambdaExpression);
    traverser.add4LambdaExpressions(lambdaExpression);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basic);
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTLambdaParameter node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTLambdaParameters node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

}
