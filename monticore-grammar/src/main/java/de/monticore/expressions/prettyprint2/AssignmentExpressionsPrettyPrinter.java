/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint2;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class AssignmentExpressionsPrettyPrinter implements AssignmentExpressionsVisitor {
  
  protected AssignmentExpressionsVisitor realThis;
  
  protected IndentPrinter printer;
  
  public AssignmentExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer=printer;
    realThis = this;
  }
  
  @Override
  public void handle(ASTIncSuffixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("++");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTDecSuffixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print("--");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTPlusPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("+");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTMinusPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("-");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTIncPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("++");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  @Override
  public void handle(ASTDecPrefixExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("--");
    node.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegularAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    if(node.getOperator()==ASTConstantsAssignmentExpressions.EQUALS) {
      getPrinter().print("=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.PERCENTEQUALS){
      getPrinter().print("%=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.ROOFEQUALS) {
      getPrinter().print("^=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.GTGTGTEQUALS){
      getPrinter().print(">>>=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.LTLTEQUALS){
      getPrinter().print("<<=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.GTGTEQUALS){
      getPrinter().print(">>=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.PIPEEQUALS){
      getPrinter().print("|=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.ANDEQUALS){
      getPrinter().print("&=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.SLASHEQUALS){
      getPrinter().print("/=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.STAREQUALS){
      getPrinter().print("*=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.PLUSEQUALS){
      getPrinter().print("+=");
    }else if(node.getOperator()==ASTConstantsAssignmentExpressions.MINUSEQUALS){
      getPrinter().print("-=");
    }
      node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public void printPlus(ASTPlusAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("+=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printMinus(ASTMinusAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("-=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printMult(ASTMultAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("*=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printDivide(ASTDivideAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("/=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printAnd(ASTAndAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("&=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printOr(ASTOrAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("|=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printBinaryXor(ASTBinaryXorAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("^=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printRightShift(ASTRightShiftAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(">>=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printLogicalRight(ASTLogicalRightAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print(">>>=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printLeftShift(ASTLeftShiftAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("<<=");
    node.getRight().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
  
  public void printModulo(ASTModuloAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    getPrinter().print("%=");
    node.getRight().accept(getRealThis());
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
  
  @Override
  public void setRealThis(AssignmentExpressionsVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public AssignmentExpressionsVisitor getRealThis() {
    return realThis;
  }
 
  
}
