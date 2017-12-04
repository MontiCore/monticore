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

import de.monticore.ocllogicexpressions._ast.ASTAnyExpr;
import de.monticore.ocllogicexpressions._ast.ASTExistsExpr;
import de.monticore.ocllogicexpressions._ast.ASTForallExpr;
import de.monticore.ocllogicexpressions._ast.ASTImpliesExpression;
import de.monticore.ocllogicexpressions._ast.ASTIterateExpr;
import de.monticore.ocllogicexpressions._ast.ASTLetDeclaration;
import de.monticore.ocllogicexpressions._ast.ASTLetinExpr;
import de.monticore.ocllogicexpressions._ast.ASTOCLDeclarationExt;
import de.monticore.ocllogicexpressions._ast.ASTSingleLogicalORExpr;
import de.monticore.ocllogicexpressions._visitor.OCLLogicExpressionsVisitor;

/**
 * @author npichler
 */

public class OCLLogicExpressionsPrettyPrinter implements OCLLogicExpressionsVisitor {
  
  protected StringBuilder sb;
  
  public OCLLogicExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
  }
  
  @Override
  public void handle(ASTImpliesExpression node) {
    node.getLeftExpression().accept(this);
    sb.append(" implies ");
    node.getRightExpression().accept(this);
  }
  
  @Override
  public void handle(ASTSingleLogicalORExpr node) {
    node.getLeft().accept(this);
    sb.append(" | ");
    node.getRight().accept(this);
  }
  
  @Override
  public void handle(ASTForallExpr node) {
    sb.append("forall ");
    if (node.getOCLCollectionVarDeclaration().isPresent()) {
      node.getOCLCollectionVarDeclaration().get().accept(this);
    }
    if (node.getOCLNestedContainer().isPresent()) {
      node.getOCLNestedContainer().get().accept(this);
    }
    sb.append(":");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTExistsExpr node) {
    sb.append("exists ");
    if (node.getOCLCollectionVarDeclaration().isPresent()) {
      node.getOCLCollectionVarDeclaration().get().accept(this);
    }
    if (node.getOCLNestedContainer().isPresent()) {
      node.getOCLNestedContainer().get().accept(this);
    }
    sb.append(":");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTAnyExpr node) {
    sb.append("any ");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLetinExpr node) {
    sb.append("let ");
    for (ASTOCLDeclarationExt ast : node.getDeclarations()) {
      ast.accept(this);
      sb.append("; ");
    }
    sb.append("in ");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTLetDeclaration node) {
    sb.append("let ");
    for (ASTOCLDeclarationExt ast : node.getDeclarations()) {
      ast.accept(this);
      sb.append(";");
    }
  }
  
  @Override
  public void handle(ASTIterateExpr node) {
    sb.append("iterate { ");
    node.getIterationDeclarator().accept(this);
    sb.append("; ");
    node.getInitDeclarator().accept(this);
    sb.append(" : ");
    sb.append(node.getAccumulatorName() + " = ");
    node.getAccumulatorValue().accept(this);
    sb.append(" }");
  }
  
}
