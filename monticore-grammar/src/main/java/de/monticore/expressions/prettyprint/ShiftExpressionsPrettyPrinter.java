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

import de.monticore.shiftexpressions._ast.ASTArrayExpression;
import de.monticore.shiftexpressions._ast.ASTLeftShiftExpression;
import de.monticore.shiftexpressions._ast.ASTLogiaclRightShiftExpression;
import de.monticore.shiftexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.shiftexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.shiftexpressions._ast.ASTRightShiftExpression;
import de.monticore.shiftexpressions._ast.ASTThisExpression;
import de.monticore.shiftexpressions._visitor.ShiftExpressionsVisitor;

/**
 * @author npichler
 */

public class ShiftExpressionsPrettyPrinter implements ShiftExpressionsVisitor {
  
  protected StringBuilder sb;
  
  public ShiftExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
  }
  
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    node.getExpression().accept(this);
    sb.append("." + node.getName());
  }
  
  @Override
  public void handle(ASTThisExpression node) {
    node.getExpression().accept(this);
    sb.append(".");
    if (node.isThis()) {
      sb.append("this");
    }
  }
  
  @Override
  public void handle(ASTArrayExpression node) {
    node.getExpression().accept(this);
    sb.append("[");
    node.getIndexExpression().accept(this);
    ;
    sb.append("]");
  }
  
  @Override
  public void handle(ASTLeftShiftExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("<<");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTRightShiftExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(">>");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLogiaclRightShiftExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(">>>");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void visit(ASTPrimaryThisExpression node) {
    sb.append("this");
  }
}
