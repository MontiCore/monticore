/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.expressions.shiftexpressions._ast.*;
import de.monticore.expressions.shiftexpressions._visitor.ShiftExpressionsVisitor;

public class ShiftExpressionsPrettyPrinter implements ShiftExpressionsVisitor{

  protected ShiftExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public ShiftExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
  }
  
   @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    getPrinter().print("this");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTArrayExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("[");
    node.getIndexExpression().accept(getRealThis());
    ;
    getPrinter().print("]");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLeftShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("<<");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(">>");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(">>>");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void visit(ASTPrimaryThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("this");
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
  public void setRealThis(ShiftExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public ShiftExpressionsVisitor getRealThis() {
    return realThis;
  }
}
