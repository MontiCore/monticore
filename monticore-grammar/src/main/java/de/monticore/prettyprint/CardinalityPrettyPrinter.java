/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.cardinality._ast.ASTCardinalityNode;
import de.monticore.cardinality._visitor.CardinalityHandler;
import de.monticore.cardinality._visitor.CardinalityTraverser;
import de.monticore.cardinality._visitor.CardinalityVisitor2;

public class CardinalityPrettyPrinter implements CardinalityVisitor2, CardinalityHandler {

  protected CardinalityTraverser traverser;

  protected IndentPrinter printer;

  public CardinalityPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTCardinality node) {
    getPrinter().print("[");
    if (node.isMany()) {
      getPrinter().print("*");
    }
    else {
      getPrinter().print(node.getLowerBound());
      if (node.getLowerBound() != node.getUpperBound() || node.isNoUpperLimit()) {
        getPrinter().print("..");
        if (node.isNoUpperLimit()) {
          getPrinter().print("*");
        }
        else {
          getPrinter().print(node.getUpperBound());
        }
      }
    }
    getPrinter().print("]");
  }

  @Override
  public CardinalityTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CardinalityTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}
