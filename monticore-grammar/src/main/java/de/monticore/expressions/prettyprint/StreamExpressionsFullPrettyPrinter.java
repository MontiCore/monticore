/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.streamexpressions.StreamExpressionsMill;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class StreamExpressionsFullPrettyPrinter extends CommonExpressionsFullPrettyPrinter {

  protected StreamExpressionsTraverser traverser;

  @Override
  public StreamExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(StreamExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  public StreamExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = StreamExpressionsMill.traverser();
    ExpressionsBasisPrettyPrinter expressionBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionBasis);
    traverser.add4ExpressionsBasis(expressionBasis);
    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressions);
    traverser.add4CommonExpressions(commonExpressions);
    StreamExpressionsPrettyPrinter streamExpressions = new StreamExpressionsPrettyPrinter(printer);
    traverser.setStreamExpressionsHandler(streamExpressions);
    traverser.add4StreamExpressions(streamExpressions);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basic);
  }


}
