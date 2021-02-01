// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.bitexpressions.BitExpressionsMill;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class BitExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  private BitExpressionsTraverser traverser;

  public BitExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = BitExpressionsMill.traverser();

    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);
    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.setBitExpressionsHandler(bitExpressions);
    traverser.add4BitExpressions(bitExpressions);
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
