package de.monticore.expressions.prettyprint;

import de.monticore.expressions.setexpressions.SetExpressionsMill;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class SetExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  private SetExpressionsTraverser traverser;

  public SetExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = SetExpressionsMill.traverser();

    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.addExpressionsBasisVisitor(basisExpression);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
    SetExpressionsPrettyPrinter setExpressions = new SetExpressionsPrettyPrinter(printer);
    traverser.setSetExpressionsHandler(setExpressions);
    traverser.addSetExpressionsVisitor(setExpressions);
  }

  @Override
  public SetExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(SetExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
