/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.cardinality._ast.ASTCardinalityNode;
import de.monticore.cardinality._visitor.CardinalityVisitor;

public class CardinalityPrettyPrinter implements CardinalityVisitor {

  private CardinalityVisitor realThis = this;

  private IndentPrinter printer;

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

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTCardinalityNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(CardinalityVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public CardinalityVisitor getRealThis() {
    return realThis;
  }

}
