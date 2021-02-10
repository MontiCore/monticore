/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class ExpressionsBasisPrettyPrinter implements ExpressionsBasisVisitor2, ExpressionsBasisHandler {

  protected ExpressionsBasisTraverser traverser;

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  protected IndentPrinter printer;

  public ExpressionsBasisPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  @Override
  public void handle(ASTNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLiteralExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLiteral().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTArguments node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    int count = 0;
    if (!node.isEmptyExpressions()) {
      for (ASTExpression ast : node.getExpressionList()) {
        if (count > 0) {
          getPrinter().print(",");
        }
        ast.accept(getTraverser());
        count++;
      }
    }
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
