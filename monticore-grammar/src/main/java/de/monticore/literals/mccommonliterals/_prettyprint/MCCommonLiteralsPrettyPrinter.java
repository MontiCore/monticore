/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

/**
 * This hand-written literals pretty printer handles the noSpace option
 * and further special cases for string literals
 */
public class MCCommonLiteralsPrettyPrinter
    extends MCCommonLiteralsPrettyPrinterTOP {

  public MCCommonLiteralsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("'" + node.getSource() + "'");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("\"" + node.getSource() + "\"");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
