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

import de.monticore.assignmentexpressions._ast.ASTAndAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTBinaryAndExpression;
import de.monticore.assignmentexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.assignmentexpressions._ast.ASTBinaryXorAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTBinaryXorExpression;
import de.monticore.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.assignmentexpressions._ast.ASTDivideAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.assignmentexpressions._ast.ASTLeftShiftAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTLogiaclRightShiftExpression;
import de.monticore.assignmentexpressions._ast.ASTMinusPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTModuloAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTMultAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTOrAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTPlusAssignmentExpression;
import de.monticore.assignmentexpressions._ast.ASTPlusPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTRightShiftAssignmentExpression;
import de.monticore.assignmentexpressions._visitor.AssignmentExpressionsVisitor;

/**
 * @author npichler
 */

public class AssignmentExpressionsPrettyPrinter implements AssignmentExpressionsVisitor {
  
  protected StringBuilder sb;
  
  public AssignmentExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
  }
  
  @Override
  public void handle(ASTIncSuffixExpression node) {
    node.getExpression().accept(this);
    sb.append("++");
  }
  
  @Override
  public void handle(ASTDecSuffixExpression node) {
    node.getExpression().accept(this);
    sb.append("--");
  }
  
  @Override
  public void handle(ASTPlusPrefixExpression node) {
    sb.append("+");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTMinusPrefixExpression node) {
    sb.append("-");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTIncPrefixExpression node) {
    sb.append("++");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTDecPrefixExpression node) {
    sb.append("--");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBinaryAndExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("&");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBinaryXorExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("^");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBinaryOrOpExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("|");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTPlusAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("+=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTMultAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("*=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTDivideAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("/=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTAndAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("&=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTOrAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("|=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTBinaryXorAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("^=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTRightShiftAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(">>=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLogiaclRightShiftExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(">>>=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLeftShiftAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("<<=");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTModuloAssignmentExpression node) {
    node.getLeftExpression().accept(this);
    sb.append("%=");
    node.getRightExpression().accept(this);
  }
  
}
