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

import de.monticore.commonexpressions._ast.ASTArguments;
import de.monticore.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.commonexpressions._ast.ASTBracketExpression;
import de.monticore.commonexpressions._ast.ASTCallExpression;
import de.monticore.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.commonexpressions._ast.ASTDivideExpression;
import de.monticore.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.commonexpressions._ast.ASTGreaterEqualExpression;
import de.monticore.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.commonexpressions._ast.ASTLessEqualExpression;
import de.monticore.commonexpressions._ast.ASTLessThanExpression;
import de.monticore.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.commonexpressions._ast.ASTMinusExpression;
import de.monticore.commonexpressions._ast.ASTMultExpression;
import de.monticore.commonexpressions._ast.ASTNotEqualsExpression;
import de.monticore.commonexpressions._ast.ASTPlusExpression;
import de.monticore.commonexpressions._ast.ASTSimpleAssignmentExpression;
import de.monticore.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressionsbasis._ast.ASTExpression;

/**
 * @author npichler
 */

public class CommonExpressionsPrettyPrinter implements CommonExpressionsVisitor {
  
  protected StringBuilder sb;
  
  public CommonExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
  }
  
  @Override
  public void handle(ASTMultExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" * ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTDivideExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" / ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTPlusExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" + ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTMinusExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" - ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLessEqualExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" <= ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTGreaterEqualExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" >= ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLessThanExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" < ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTGreaterThanExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" > ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" && ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" || ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSimpleAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" += ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTEqualsExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" == ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTNotEqualsExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" != ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTConditionalExpression node) {
    node.getCondition().accept(this);
    sb.append(" ? ");
    node.getTrueExpression().accept(this);
    sb.append(" : ");
    node.getFalseExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBracketExpression node) {
    sb.append("(");
    node.getExpression().accept(this);
    sb.append(")");
  }
  
  @Override
  public void handle(ASTBooleanNotExpression node) {
    sb.append("~");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLogicalNotExpression node) {
    sb.append("!");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTArguments node) {
    sb.append("(");
    int count = 0;
    if (!node.getExpressions().isEmpty()) {
      for (ASTExpression ast : node.getExpressions()) {
        if (count > 0) {
          sb.append(",");
        }
        ast.accept(this);
        count++;
      }
    }
    sb.append(")");
  }
  
  @Override
  public void handle(ASTCallExpression node) {
    node.getExpression().accept(this);
    handle(node.getArguments());
  }
}
