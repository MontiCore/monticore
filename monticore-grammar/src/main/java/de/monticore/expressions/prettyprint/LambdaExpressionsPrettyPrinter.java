/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpressionBody;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameters;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsHandler;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class LambdaExpressionsPrettyPrinter
    implements LambdaExpressionsVisitor2, LambdaExpressionsHandler {

  protected LambdaExpressionsTraverser traverser;

  @Override
  public void setTraverser(LambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public LambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  protected IndentPrinter printer;

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public LambdaExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTLambdaParameter node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentMCType()) {
      node.getMCType().accept(getTraverser());
      getPrinter().print(" ");
    }
    getPrinter().print(node.getName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLambdaParameters node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentParenthesis()) {
      getPrinter().print("(");
    }
    for (int i = 0; i < node.getLambdaParameterList().size(); i++) {
      node.getLambdaParameterList().get(i).accept(getTraverser());
      if (i != node.getLambdaParameterList().size() - 1) {
        getPrinter().print(",");
      }
    }
    if (node.isPresentParenthesis()) {
      getPrinter().print(")");
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLambdaExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLambdaParameters().accept(getTraverser());
    getPrinter().print(" -> ");
    node.getLambdaBody().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTLambdaExpressionBody node) {
    node.getExpression().accept(getTraverser());
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

}
