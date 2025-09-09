/* (c) https://github.com/MontiCore/monticore */
package de.monticore.completeness._prettyprint;

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.prettyprint.IndentPrinter;

public class CompletenessPrettyPrinter extends CompletenessPrettyPrinterTOP {

  public CompletenessPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTCompleteness node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
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
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
