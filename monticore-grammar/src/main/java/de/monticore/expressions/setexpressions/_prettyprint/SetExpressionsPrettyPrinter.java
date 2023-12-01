// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.setexpressions._prettyprint;

import de.monticore.expressions.setexpressions._ast.ASTSetCollectionItem;
import de.monticore.expressions.setexpressions._ast.ASTSetComprehension;
import de.monticore.expressions.setexpressions._ast.ASTSetComprehensionItem;
import de.monticore.expressions.setexpressions._ast.ASTSetEnumeration;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class SetExpressionsPrettyPrinter extends SetExpressionsPrettyPrinterTOP {

  public SetExpressionsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  // Following are implementations not given by the autogenerated printer

  @Override
  public void handle(ASTSetComprehension node) {
    if (isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentSet()) {
      getPrinter().print("Set");
    }
    getPrinter().print(node.getOpeningBracket());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" | ");
    for (ASTSetComprehensionItem setComprehensionItem : node.getSetComprehensionItemList()) {
      setComprehensionItem.accept(getTraverser());
      if (!node.getSetComprehensionItemList()
          .get(node.getSetComprehensionItemList().size() - 1)
          .equals(setComprehensionItem)) {
        getPrinter().print(", ");
      }
    }
    if (node.getOpeningBracket().equals("{")) {
      getPrinter().print("}");
    }
    else {
      getPrinter().print("]");
    }
    if (isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTSetEnumeration node) {
    if (isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentSet()) {
      getPrinter().print("Set");
    }
    getPrinter().print(node.getOpeningBracket());
    for (ASTSetCollectionItem setCollectionItem : node.getSetCollectionItemList()) {
      setCollectionItem.accept(getTraverser());
      if (!node.getSetCollectionItemList()
          .get(node.getSetCollectionItemList().size() - 1)
          .equals(setCollectionItem)) {
        getPrinter().print(", ");
      }
    }
    if (node.getOpeningBracket().equals("{")) {
      getPrinter().print("}");
    }
    else {
      getPrinter().print("]");
    }
    if (isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
