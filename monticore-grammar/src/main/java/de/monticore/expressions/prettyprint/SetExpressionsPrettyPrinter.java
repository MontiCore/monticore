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
  
  protected StringBuilder sb;
  
  public SetExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
  }
  
  @Override
  public void handle(ASTIsInExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" isin ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSetInExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" in ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTUnionExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" union ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTIntersectionExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" intersect ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSetAndExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" setand ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSetOrExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" setor ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSetXOrExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" setxor ");
    node.getRightExpression().accept(this);
  }
  
}
