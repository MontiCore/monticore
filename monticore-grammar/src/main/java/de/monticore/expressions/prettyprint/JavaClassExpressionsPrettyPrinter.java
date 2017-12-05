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
import de.monticore.javaclassexpressions._ast.ASTArguments;
import de.monticore.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericSuperInvocationSuffix;
import de.monticore.javaclassexpressions._ast.ASTInstanceofExpression;
import de.monticore.javaclassexpressions._ast.ASTNameExpression;
import de.monticore.javaclassexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.javaclassexpressions._ast.ASTTypeCastExpression;
import de.monticore.javaclassexpressions._visitor.JavaClassExpressionsVisitor;

/**
 * @author npichler
 */

public class JavaClassExpressionsPrettyPrinter implements JavaClassExpressionsVisitor {
  
  protected StringBuilder sb;
  
  public JavaClassExpressionsPrettyPrinter() {
    sb = new StringBuilder();
  }
  
  public String toString() {
    return sb.toString();
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
  public void visit(ASTPrimarySuperExpression node) {
    sb.append("super");
  }
  
  @Override
  public void handle(ASTSuperSuffix node) {
    if (node.getName().isPresent()) {
      sb.append(".");
      if (node.getETypeArguments().isPresent()) {
        node.getETypeArguments().get().accept(this);
      }
      sb.append(node.getName().get());
      if (node.getArguments().isPresent()) {
        node.getArguments().get().accept(this);
        ;
      }
    }
    else {
      node.getArguments().get().accept(this);
      ;
    }
  }
  
  @Override
  public void handle(ASTSuperExpression node) {
    node.getExpression().accept(this);
    sb.append(".super");
    node.getSuperSuffix().accept(this);
  }
  
  @Override
  public void handle(ASTClassExpression node) {
    node.getEReturnType().accept(this);
    sb.append(".class");
  }
  
  @Override
  public void handle(ASTTypeCastExpression node) {
    sb.append("(");
    node.getEType().accept(this);
    sb.append(")");
    node.getExpression().accept(this);
  }
  
  @Override
  public void handle(ASTGenericSuperInvocationSuffix node) {
    if (node.isSuper()) {
      sb.append("super");
    }
    node.getSuperSuffix().accept(this);
  }
  
  @Override
  public void handle(ASTGenericInvocationExpression node) {
    node.getExpression().accept(this);
    sb.append(".");
    handle(node.getPrimaryGenericInvocationExpression());
  }
  
  @Override
  public void handle(ASTNameExpression node) {
   sb.append(node.getName());
  }
  
  @Override
  public void handle(ASTInstanceofExpression node) {
    node.getExpression().accept(this);
    sb.append(" instanceof ");
    node.getEType().accept(this);
  }
  
}
