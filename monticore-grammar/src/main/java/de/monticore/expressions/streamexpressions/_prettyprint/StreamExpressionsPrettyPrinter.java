/* (c) https://github.com/MontiCore/monticore */

package de.monticore.expressions.streamexpressions._prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTEventStreamConstructorExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

public class StreamExpressionsPrettyPrinter extends StreamExpressionsPrettyPrinterTOP {

  public StreamExpressionsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTEventStreamConstructorExpression node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isTiming()) {
      getPrinter().print("Event");
    }
    getPrinter().print("<");
    List<List<ASTExpression>> expressionsPerTimeSlice =
        node.getExpressionsPerTimeSlice();
    for (int i = 0; i < expressionsPerTimeSlice.size(); i++) {
      if (i != 0) {
        getPrinter().print(";");
      }
      for (int j = 0; j < expressionsPerTimeSlice.get(i).size(); j++) {
        if (j != 0) {
          getPrinter().print(",");
        }
        expressionsPerTimeSlice.get(i).get(j).accept(getTraverser());
      }
    }
    getPrinter().print(">");
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

}


