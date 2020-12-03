// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsHandler;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class JavaClassExpressionsPrettyPrinter implements JavaClassExpressionsVisitor2, JavaClassExpressionsHandler {

  protected JavaClassExpressionsTraverser traverser;

  @Override
  public void setTraverser(JavaClassExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public JavaClassExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  protected IndentPrinter printer;

  public JavaClassExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
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
      if (!node.getExtTypeArgumentList().isEmpty()) {
        getPrinter().print("<");
        for (int i = 0; i < node.getExtTypeArgumentList().size(); i++) {
          node.getExtTypeArgument(i).accept(getTraverser());
          if (i != node.getExtTypeArgumentList().size() - 1) {
            getPrinter().print(",");
          }
        }
        getPrinter().print(">");
      }
      getPrinter().print(node.getName());
      if (node.isPresentArguments()) {
        node.getArguments().accept(getTraverser());
      }
    } else {
      node.getArguments().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSuperExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(".super");
    node.getSuperSuffix().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTClassExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExtReturnType().accept(getTraverser());
    getPrinter().print(".class");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTTypeCastExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExtType().accept(getTraverser());
    getPrinter().print(")");
    node.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTGenericInvocationSuffix node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentSuperSuffix()) {
      if (node.isSuper()) {
        getPrinter().print("super");
      }
      node.getSuperSuffix().accept(getTraverser());
    } else if (node.isPresentName()) {
      getPrinter().print(node.getName());
      node.getArguments().accept(getTraverser());
    } else {
      if (node.isThis()) {
        getPrinter().print("this");
      }
      node.getArguments().accept(getTraverser());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  @Override
  public void handle(ASTGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(".");
    handle(node.getPrimaryGenericInvocationExpression());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTPrimaryGenericInvocationExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("<");
    for (int i = 0; i < node.getExtTypeArgumentList().size(); i++) {
      node.getExtTypeArgument(i).accept(getTraverser());
      if (i != node.getExtTypeArgumentList().size() - 1) {
        getPrinter().print(",");
      }
    }
    getPrinter().print(">");
    getPrinter().print(" ");
    node.getGenericInvocationSuffix().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTInstanceofExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(" instanceof ");
    node.getExtType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print(".");
    getPrinter().print("this");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTArrayExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getTraverser());
    getPrinter().print("[");
    node.getIndexExpression().accept(getTraverser());
    ;
    getPrinter().print("]");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void visit(ASTPrimaryThisExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("this");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTArrayCreator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getExtType().accept(getTraverser());
    a.getArrayDimensionSpecifier().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayDimensionByExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    for (ASTExpression astExpression : a.getExpressionList()) {
      getPrinter().print("[");
      astExpression.accept(getTraverser());
      getPrinter().print("]");
    }
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTCreatorExpression a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" new ");
    a.getCreator().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTGenericInvocationSuffix node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTSuperSuffix node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }


}
