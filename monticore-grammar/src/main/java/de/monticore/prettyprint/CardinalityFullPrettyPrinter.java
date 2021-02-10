/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinalityNode;
import de.monticore.cardinality._visitor.CardinalityTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;

public class CardinalityFullPrettyPrinter {

  private CardinalityTraverser traverser;

  protected IndentPrinter printer;

  public CardinalityFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = CardinalityMill.traverser();

    CardinalityPrettyPrinter cardinality = new CardinalityPrettyPrinter(printer);
    traverser.add4Cardinality(cardinality);
    traverser.setCardinalityHandler(cardinality);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
  }

  public CardinalityTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CardinalityTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public String prettyprint(ASTCardinalityNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }


}
