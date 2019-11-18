// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.expressions.setexpressions._ast.*;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;

public class SetExpressionsPrettyPrinter extends ExpressionsBasisPrettyPrinter implements SetExpressionsVisitor {
  
  protected SetExpressionsVisitor realThis;
  
  public SetExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    realThis = this;
  }
  
  @Override
  public void handle(ASTIsInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getElem().accept(getRealThis());
    getPrinter().print(" isin ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getElem().accept(getRealThis());
    getPrinter().print(" in ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTUnionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" union ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTIntersectionExpressionInfix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(" intersect ");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetAndExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("setand ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetOrExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("setor ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTUnionExpressionPrefix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("union ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIntersectionExpressionPrefix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("intersect ");
    node.getSet().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
 
  @Override
  public void setRealThis(SetExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public SetExpressionsVisitor getRealThis() {
    return realThis;
  }
  
}
