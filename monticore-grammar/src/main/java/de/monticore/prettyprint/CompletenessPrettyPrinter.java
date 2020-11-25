/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.completeness._ast.ASTCompletenessNode;
import de.monticore.completeness._visitor.CompletenessHandler;
import de.monticore.completeness._visitor.CompletenessTraverser;
import de.monticore.completeness._visitor.CompletenessVisitor2;

public class CompletenessPrettyPrinter implements CompletenessVisitor2, CompletenessHandler {

  protected CompletenessTraverser traverser;

  private IndentPrinter printer;

  public CompletenessPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTCompleteness node) {
    if (node.isComplete()) {
      getPrinter().print("(c)");
    }
    else if (node.isIncomplete()) {
      getPrinter().print("(...)");
    }
    else if (node.isLeftComplete()) {
      getPrinter().print("(c,...)");
    }
    else if (node.isRightComplete()) {
      getPrinter().print("(...,c)");
    }
  }

  @Override
  public CompletenessTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CompletenessTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}
