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
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.javaclassexpressions._ast.ASTInstanceofExpression;
import de.monticore.javaclassexpressions._ast.ASTNameExpression;
import de.monticore.javaclassexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.javaclassexpressions._ast.ASTTypeCastExpression;
import de.monticore.javaclassexpressions._visitor.JavaClassExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

/**
 * @author npichler
 */

public class JavaClassExpressionsPrettyPrinter implements JavaClassExpressionsVisitor {
  
 protected JavaClassExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public JavaClassExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer=printer;
    realThis = this;

  }

  @Override
  public void visit(ASTPrimarySuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("super");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSuperSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentName()) {
      getPrinter().print(".");
      if (node.isPresentETypeArguments()) {
        node.getETypeArguments().accept(getRealThis());
      }
      getPrinter().print(node.getName());
      if (node.isPresentArguments()) {
        node.getArguments().accept(getRealThis());
        ;
      }
    }
    else {
      node.getArguments().accept(getRealThis());
      ;
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".super");
    node.getSuperSuffix().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTClassExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getEReturnType().accept(getRealThis());
    getPrinter().print(".class");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getEType().accept(getRealThis());
    getPrinter().print(")");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTGenericInvocationSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if(node.isPresentSuperSuffix()) {
      if (node.isSuper()) {
        getPrinter().print("super");
      }
      node.getSuperSuffix().accept(getRealThis());;
    }else if(node.isPresentName()) {
      getPrinter().print(node.getName());
      node.getArguments().accept(getRealThis());
    }else {
      if (node.isThis()) {
        getPrinter().print("this");
      }
      node.getArguments().accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
    
  }
  
  @Override
  public void handle(ASTGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    handle(node.getPrimaryGenericInvocationExpression());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTNameExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
   getPrinter().print(node.getName());
   CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTInstanceofExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(" instanceof ");
    node.getEType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTArguments node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    int count = 0;
    if (!node.isEmptyExpressions()) {
      for (ASTExpression ast : node.getExpressionList()) {
        if (count > 0) {
          getPrinter().print(",");
        }
        ast.accept(getRealThis());
        count++;
      }
    }
    getPrinter().print(")");
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
  
  public String prettyprint(ASTGenericInvocationSuffix node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
  public String prettyprint(ASTSuperSuffix node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
  @Override
  public void setRealThis(JavaClassExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public JavaClassExpressionsVisitor getRealThis() {
    return realThis;
  }
  
}
