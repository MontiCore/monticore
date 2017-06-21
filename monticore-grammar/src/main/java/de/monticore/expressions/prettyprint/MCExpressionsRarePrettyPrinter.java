/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.expressions.prettyprint;

import java.util.Iterator;

import de.monticore.expressions.mcexpressionsrare._ast.ASTAddExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTArguments;
import de.monticore.expressions.mcexpressionsrare._ast.ASTArrayExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTAssignmentExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBinaryAndOpExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBinaryXorOpExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBooleanNotExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTBracketExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTCallExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTComparisonExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTConditionalExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTIdentityExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTLogicalNotExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTMCExpressionsRareNode;
import de.monticore.expressions.mcexpressionsrare._ast.ASTMultExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTNameExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTPrefixExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTShiftExpression;
import de.monticore.expressions.mcexpressionsrare._ast.ASTSuffixExpression;
import de.monticore.expressions.mcexpressionsrare._visitor.MCExpressionsRareVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class MCExpressionsRarePrettyPrinter implements MCExpressionsRareVisitor {

  private IndentPrinter printer = null;

  private boolean WRITE_COMMENTS = false;
  
  private MCExpressionsRareVisitor realThis = this;

  public MCExpressionsRarePrettyPrinter(IndentPrinter out) {
    super();
    this.printer = out;
    setWriteComments(true);
  }

  /**
   * @return the printer
   */
  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printDimensions(int dims) {
    for (int i = 0; i < dims; i++) {
      getPrinter().print("[]");
    }
  }

  @Override
  public void handle(ASTBracketExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    a.getExpression().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTNameExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printNode(a.getName());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTQualifiedNameExpression)
   */
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    printNode(node.getName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTArrayExpression)
   */
  @Override
  public void handle(ASTArrayExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("[");
    node.getIndexExpression().accept(getRealThis());
    getPrinter().print("]");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTCallExpression)
   */
  @Override
  public void handle(ASTCallExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    node.getArguments().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuffixExpression)
   */
  @Override
  public void handle(ASTSuffixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(node.getSuffixOp().orElse(""));
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTPrefixExpression)
   */
  @Override
  public void handle(ASTPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getPrefixOp().orElse(""));
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanNotExpression)
   */
  @Override
  public void handle(ASTBooleanNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("~");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTLogicalNotExpression)
   */
  @Override
  public void handle(ASTLogicalNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("!");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTMultExpression)
   */
  @Override
  public void handle(ASTMultExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getMultiplicativeOp().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTAddExpression)
   */
  @Override
  public void handle(ASTAddExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getAdditiveOp().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTShiftExpression)
   */
  @Override
  public void handle(ASTShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getShiftOp().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTComparisonExpression)
   */
  @Override
  public void handle(ASTComparisonExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getComparison().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }


  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTIdentityExpression)
   */
  @Override
  public void handle(ASTIdentityExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getIdentityTest().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryAndOpExpression)
   */
  @Override
  public void handle(ASTBinaryAndOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("&");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryXorOpExpression)
   */
  @Override
  public void handle(ASTBinaryXorOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("^");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBinaryOrOpExpression)
   */
  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("|");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanAndOpExpression)
   */
  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("&&");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTBooleanOrOpExpression)
   */
  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("||");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTConditionalExpression)
   */
  @Override
  public void handle(ASTConditionalExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getCondition().accept(getRealThis());
    getPrinter().print("?");
    node.getTrueExpression().accept(getRealThis());
    getPrinter().print(": ");
    node.getFalseExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTAssignmentExpression)
   */
  @Override
  public void handle(ASTAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(node.getAssignment().orElse(""));
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTArguments a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("(");
    printExpressionsList(a.getExpressions().iterator(), ", ");
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  protected void printNode(String s) {
    getPrinter().print(s);
  }

  protected void printExpressionsList(Iterator<? extends ASTMCExpressionsRareNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  public void setWriteComments(boolean wc) {
    WRITE_COMMENTS = wc;
  }

  public boolean isWriteCommentsEnabeled() {
    return WRITE_COMMENTS;
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCExpressionsRareNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCExpressionsRareVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExpressionsRareVisitor getRealThis() {
    return realThis;
  }
  
  
}
