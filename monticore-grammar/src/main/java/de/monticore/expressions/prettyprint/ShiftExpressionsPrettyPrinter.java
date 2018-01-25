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

import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.shiftexpressions._ast.ASTArrayExpression;
import de.monticore.shiftexpressions._ast.ASTLeftShiftExpression;
import de.monticore.shiftexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.shiftexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.shiftexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.shiftexpressions._ast.ASTRightShiftExpression;
import de.monticore.shiftexpressions._ast.ASTThisExpression;
import de.monticore.shiftexpressions._visitor.ShiftExpressionsVisitor;

/**
 * @author npichler
 */

public class ShiftExpressionsPrettyPrinter implements ShiftExpressionsVisitor{

  protected ShiftExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public ShiftExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
  }
  
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("." + node.getName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    if (node.isThis()) {
      getPrinter().print("this");
    }
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
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print("<<");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(">>");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLogicalRightShiftExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(">>>");
    node.getRightExpression().accept(getRealThis());
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
