/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.expressions.bitexpressions._ast.*;

public class BitExpressionsPrettyPrinter implements BitExpressionsVisitor2, BitExpressionsHandler {

  protected BitExpressionsTraverser traverser;
  
  protected IndentPrinter printer;
  
  public BitExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(BitExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTLeftShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("<<");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(">>");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(">>>");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTBinaryAndExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("&");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBinaryXorExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("^");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print("|");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }
}
