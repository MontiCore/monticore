package de.monticore.expressions.prettyprint;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class CommonExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {
  private CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public CommonExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = CommonExpressionsMill.traverser();

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressions);
    traverser.addCommonExpressionsVisitor(commonExpressions);
    ExpressionsBasisPrettyPrinter basicExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basicExpression);
    traverser.addExpressionsBasisVisitor(basicExpression);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basic);
  }
}

