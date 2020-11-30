package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class ExpressionsBasisFullPrettyPrinter {
  private ExpressionsBasisTraverser traverser;

  protected IndentPrinter printer;

  public ExpressionsBasisFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.traverser = ExpressionsBasisMill.traverser();

    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.addExpressionsBasisVisitor(basisExpression);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basics);
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}
