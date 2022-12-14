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

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("'" + node.getSource() + "'");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("\"" + node.getSource() + "\"");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentDigits()) {
      if (node.isNegative()) {
        getPrinter().print("-"); // nospace directive
      }
      getPrinter().print(node.getDigits() + " ");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print(node.getDigits()); // nospace
    getPrinter().print("l ");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isNegative()) {
      getPrinter().print("-"); // nospace
    }
    getPrinter().print(node.getDigits()); // nospace
    getPrinter().print("l ");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print(node.getPre());// nospace
    getPrinter().print("."); // nospace
    getPrinter().print(node.getPost()); // nospace
    getPrinter().print("f ");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isNegative()) {
      getPrinter().print("-"); // nospace
    }
    getPrinter().print(node.getPre());  // nospace
    getPrinter().print(".");  // nospace
    getPrinter().print(node.getPost());  // nospace
    getPrinter().print("f ");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print(node.getPre());// nospace
    getPrinter().print("."); // nospace
    getPrinter().print(node.getPost() + " ");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isNegative()) {
      getPrinter().print("-"); // nospace
    }
    getPrinter().print(node.getPre());  // nospace
    getPrinter().print(".");  // nospace
    getPrinter().print(node.getPost() + " ");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}
