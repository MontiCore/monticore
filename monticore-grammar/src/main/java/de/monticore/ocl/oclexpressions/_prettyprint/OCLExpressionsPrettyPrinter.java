// (c) https://github.com/MontiCore/monticore

package de.monticore.ocl.oclexpressions._prettyprint;

import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;

public class OCLExpressionsPrettyPrinter extends OCLExpressionsPrettyPrinterTOP {

  public OCLExpressionsPrettyPrinter(
      IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  // The following overriden methods are required,
  // as the auto generation of pretty printers failed here

  @Override
  public void handle(ASTInDeclaration node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentMCType()) {
      node.getMCType().accept(getTraverser());
    }

    Iterator<ASTInDeclarationVariable> astInDeclarationIterator =
        node.iteratorInDeclarationVariables();
    astInDeclarationIterator.next().accept(getTraverser());
    while (astInDeclarationIterator.hasNext()) {
      getPrinter().print(", ");
      astInDeclarationIterator.next().accept(getTraverser());
    }

    if (node.isPresentExpression()) {
      getPrinter().print("in ");
      node.getExpression().accept(getTraverser());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
