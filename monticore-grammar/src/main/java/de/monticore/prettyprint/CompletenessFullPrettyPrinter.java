/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.completeness.CompletenessMill;
import de.monticore.completeness._ast.ASTCompletenessNode;
import de.monticore.completeness._visitor.CompletenessTraverser;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;

public class CompletenessFullPrettyPrinter {

  private CompletenessTraverser traverser;

  protected IndentPrinter printer;

  public CompletenessFullPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.traverser = CompletenessMill.traverser();

    CompletenessPrettyPrinter completeness = new CompletenessPrettyPrinter(printer);
    traverser.add4Completeness(completeness);
    traverser.setCompletenessHandler(completeness);
  }

  public CompletenessTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CompletenessTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public String prettyprint(ASTCompletenessNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
