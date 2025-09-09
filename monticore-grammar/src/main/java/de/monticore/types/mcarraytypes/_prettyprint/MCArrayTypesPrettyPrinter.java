/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcarraytypes._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;

public class MCArrayTypesPrettyPrinter extends MCArrayTypesPrettyPrinterTOP {

  public MCArrayTypesPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCArrayType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    // The action + astrule combination of dimension requires manual work
    node.getMCType().accept(getTraverser());
    for (int i = 0; i < node.getDimensions(); i++) {
      getPrinter().print("[]");
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
