/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConcatStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTEmptyStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTLengthStreamExpression;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsHandler;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsTraverser;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class StreamExpressionsPrettyPrinter implements StreamExpressionsVisitor2, StreamExpressionsHandler {

  protected StreamExpressionsTraverser traverser;

  @Override
  public void setTraverser(StreamExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public StreamExpressionsTraverser getTraverser() {
    return traverser;
  }

  protected IndentPrinter printer;

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public StreamExpressionsPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
  }

  @Override
  public void handle(ASTEmptyStreamExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("<>");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTAppendStreamExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(":~");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTConcatStreamExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("^~");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLengthStreamExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("#");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
