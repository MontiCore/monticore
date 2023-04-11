/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {

  public CommonExpressionsJavaPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    node.getExpression().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(".");
    String name = "get" + node.getName().substring(0, 1).toUpperCase() + node.getName().substring(1) + "()";
    getPrinter().print(name);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  /**
   * Apply this overridden handling of FieldAccessExpressions to a FullPrettyPrinter
   * @param traverser the FullPrettyPrinters traverser
   * @param indentPrinter the printer to use
   * @param printComments whether to print comments
   */
  public static <T extends CommonExpressionsTraverser> void applyJavaPrinter(T traverser, IndentPrinter indentPrinter, boolean printComments){
    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(indentPrinter, printComments);
    traverser.getCommonExpressionsVisitorList().clear();
    traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
    traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
  }
  
}
