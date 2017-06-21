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

import de.monticore.expressions.mcexpressions._ast.ASTClassExpression;
import de.monticore.expressions.mcexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.expressions.mcexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.expressions.mcexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.mcexpressions._ast.ASTLiteralExpression;
import de.monticore.expressions.mcexpressions._ast.ASTMCExpressionsNode;
import de.monticore.expressions.mcexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.expressions.mcexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.expressions.mcexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.expressions.mcexpressions._ast.ASTSuperExpression;
import de.monticore.expressions.mcexpressions._ast.ASTSuperSuffix;
import de.monticore.expressions.mcexpressions._ast.ASTThisExpression;
import de.monticore.expressions.mcexpressions._ast.ASTTypeCastExpression;
import de.monticore.expressions.mcexpressions._visitor.MCExpressionsVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class MCExpressionsPrettyPrinter extends MCExpressionsRarePrettyPrinter implements MCExpressionsVisitor {

  private boolean WRITE_COMMENTS = false;
  
  private MCExpressionsVisitor realThis = this;

  public MCExpressionsPrettyPrinter(IndentPrinter out) {
    super(out);
    setWriteComments(true);
  }

  @Override
  public void handle(ASTPrimaryThisExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("this");   
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTPrimarySuperExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("super");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLiteralExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLiteral().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTClassExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getReturnType().accept(getRealThis());
    getPrinter().print(".class");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTPrimaryGenericInvocationExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getTypeArguments().accept(getRealThis());
    a.getGenericInvocationSuffix().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTThisExpression)
   */
  @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".this ");
    CommentPrettyPrinter.printPostComments(node, getPrinter());

  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuperExpression)
   */
  @Override
  public void handle(ASTSuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".super ");
    node.getSuperSuffix().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTGenericInvocationExpression)
   */
  @Override
  public void handle(ASTGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    node.getPrimaryGenericInvocationExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }


  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTTypeCastExpression)
   */
  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getType().accept(getRealThis());
    getPrinter().print(")");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTInstanceofExpression)
   */
  @Override
  public void handle(ASTInstanceofExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(" instanceof ");
    node.getType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }


  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTGenericInvocationSuffix)
   */
  @Override
  public void handle(ASTGenericInvocationSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isSuper()) {
      getPrinter().print(" super ");
      node.getSuperSuffix().get().accept(getRealThis());
    }
    if (node.isThis()) {
      getPrinter().print(" this ");
      node.getSuperSuffix().get().accept(getRealThis());
    }
    if (node.getName().isPresent()) {
      printNode(node.getName().get());
      node.getArguments().get().accept(getRealThis());    
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTSuperSuffix)
   */
  @Override
  public void handle(ASTSuperSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.getName().isPresent()) {
      getPrinter().print(".");
      if (node.getTypeArguments().isPresent()) {
        node.getTypeArguments().get().accept(getRealThis());
      }
      printNode(node.getName().get());
    }
    if (node.getArguments().isPresent()) {
      node.getArguments().get().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  protected void printNode(String s) {
    getPrinter().print(s);
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
  public String prettyprint(ASTMCExpressionsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExpressionsVisitor getRealThis() {
    return realThis;
  }
  
  
}
