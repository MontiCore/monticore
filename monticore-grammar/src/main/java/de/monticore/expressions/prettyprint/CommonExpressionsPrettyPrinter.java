/* (c) https://github.com/MontiCore/monticore */
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
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

/**
 * @author npichler
 */

public class CommonExpressionsPrettyPrinter implements CommonExpressionsVisitor {
  
  protected CommonExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public CommonExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
  }
  
  @Override
  public void handle(ASTMultExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" * ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTDivideExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" / ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTPlusExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" + ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTMinusExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" - ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLessEqualExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" <= ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTGreaterEqualExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" >= ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLessThanExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" < ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTGreaterThanExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" > ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" && ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" || ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTSimpleAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" += ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTEqualsExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" == ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTNotEqualsExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeftExpression().accept(getRealThis());
    getPrinter().print(" != ");
    node.getRightExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTConditionalExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getCondition().accept(getRealThis());
    getPrinter().print(" ? ");
    node.getTrueExpression().accept(getRealThis());
    getPrinter().print(" : ");
    node.getFalseExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTBracketExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getExpression().accept(getRealThis());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTBooleanNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("~");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTLogicalNotExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("!");
    node.getExpression().accept(getRealThis());
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
  
  @Override
  public void handle(ASTCallExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    handle(node.getArguments());
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
  
  public String prettyprint(ASTArguments node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
  @Override
  public void setRealThis(CommonExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public CommonExpressionsVisitor getRealThis() {
    return realThis;
  }
}
