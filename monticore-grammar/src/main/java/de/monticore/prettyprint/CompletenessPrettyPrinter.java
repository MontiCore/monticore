/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.completeness._ast.ASTCompletenessNode;
import de.monticore.completeness._visitor.CompletenessVisitor;

public class CompletenessPrettyPrinter implements CompletenessVisitor {
  
  private IndentPrinter printer = null;
  
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
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTCompletenessNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
