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
import de.monticore.setexpressions._ast.ASTIntersectionExpression;
import de.monticore.setexpressions._ast.ASTIsInExpression;
import de.monticore.setexpressions._ast.ASTSetAndExpression;
import de.monticore.setexpressions._ast.ASTSetInExpression;
import de.monticore.setexpressions._ast.ASTSetOrExpression;
import de.monticore.setexpressions._ast.ASTSetXOrExpression;
import de.monticore.setexpressions._ast.ASTUnionExpression;
import de.monticore.setexpressions._visitor.SetExpressionsVisitor;

/**
 * @author npichler
 */
public class SetExpressionsPrettyPrinter implements SetExpressionsVisitor {
  
  protected SetExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public SetExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
  }
  
  @Override
  public void handle(ASTIsInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" isin ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetInExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" in ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTUnionExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" union ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTIntersectionExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" intersect ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetAndExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" setand ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetOrExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" setor ");
    node.getRightExpression().accept(this);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSetXOrExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(this);
    getPrinter().print(" setxor ");
    node.getRightExpression().accept(this);
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
