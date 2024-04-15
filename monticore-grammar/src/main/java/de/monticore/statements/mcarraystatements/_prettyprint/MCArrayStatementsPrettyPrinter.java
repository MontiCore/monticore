/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcarraystatements._prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;

import java.util.Iterator;

public class MCArrayStatementsPrettyPrinter extends MCArrayStatementsPrettyPrinterTOP {

  public MCArrayStatementsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public  void handle (de.monticore.statements.mcarraystatements._ast.ASTArrayInit node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Iterator<ASTVariableInit> iter_variableInit = node.getVariableInitList().iterator();
    getPrinter().println("{ ");
    getPrinter().indent();

    if ( iter_variableInit.hasNext() ) {
      iter_variableInit.next().accept(getTraverser());
      while  ( iter_variableInit.hasNext() ) {
        getPrinter().stripTrailing();
        getPrinter().print(",");
        iter_variableInit.next().accept(getTraverser());
      }
    }
    getPrinter().stripTrailing();
    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("} ");

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
