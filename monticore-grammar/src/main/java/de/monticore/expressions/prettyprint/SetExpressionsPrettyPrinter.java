// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.expressions.setexpressions._ast.*;

public class SetExpressionsPrettyPrinter implements SetExpressionsVisitor2, SetExpressionsHandler {
  
  protected SetExpressionsTraverser traverser;
  
  protected IndentPrinter printer;
  
  public SetExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public SetExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SetExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTIsInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getElem().accept(getTraverser());
    getPrinter().print(" isin ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getElem().accept(getTraverser());
    getPrinter().print(" in ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTUnionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" union ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTIntersectionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" intersect ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetAndExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("setand ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetOrExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("setor ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTUnionExpressionPrefix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("union ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIntersectionExpressionPrefix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("intersect ");
    node.getSet().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
}
