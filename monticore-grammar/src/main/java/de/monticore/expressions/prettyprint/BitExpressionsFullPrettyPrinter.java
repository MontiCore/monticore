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
    traverser.addExpressionsBasisVisitor(basisExpression);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.setBitExpressionsHandler(bitExpressions);
    traverser.addBitExpressionsVisitor(bitExpressions);
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
