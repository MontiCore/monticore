/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals._prettyprint;

import de.monticore.prettyprint.FormattingPrinter;

/**
 * This hand-written literals formatting pretty printer handles the 
 * special cases for wrapping string and char literals with the correct quotes.
 */
public class MCCommonLiteralsFormattingPrettyPrinter
    extends MCCommonLiteralsFormattingPrettyPrinterTOP {

  public MCCommonLiteralsFormattingPrettyPrinter(FormattingPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().startProduction("CharLiteral");
    getPrinter().emit("'" + node.getSource() + "'", "Char", "0");
    getPrinter().endProduction();

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().startProduction("StringLiteral");
    getPrinter().emit("\"" + node.getSource() + "\"", "String", "0");
    getPrinter().endProduction();

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}