/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcoptionaltypes._prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcoptionaltypes._ast.ASTMCShortOptionalType;

public class MCOptionalTypesPrettyPrinter
    extends MCOptionalTypesPrettyPrinterTOP {

  public MCOptionalTypesPrettyPrinter(IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }

  /**
   * prints {@code T?} without whitespace between the type and the "?"
   * (the generated pretty printer would print {@code T ?}).
   * A trailing space is printed afterward (like the generated printers),
   * s.t., e.g., {@code Person? p} is not printed as {@code Person?p}.
   */
  @Override
  public void handle(ASTMCShortOptionalType node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getMCType().accept(getTraverser());
    // the inner type may end with a space (e.g., after names)
    getPrinter().stripTrailing();
    getPrinter().print("? ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
