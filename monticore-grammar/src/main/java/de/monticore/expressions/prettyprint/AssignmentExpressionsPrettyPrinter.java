// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class AssignmentExpressionsPrettyPrinter extends ExpressionsBasisPrettyPrinter implements AssignmentExpressionsVisitor {
  
  protected AssignmentExpressionsVisitor realThis;
  

  public AssignmentExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
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
  public void handle(ASTAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getRealThis());
    // ["="|"+="|"-="|"*="|"/="|"&="|"|="|"^="|">>="|">>>="|"<<="|"%="]
    switch (node.getOperator()) {
      case ASTConstantsAssignmentExpressions.EQUALS:
        getPrinter().print(("="));
        break;
      case ASTConstantsAssignmentExpressions.PLUSEQUALS:
        getPrinter().print(("+="));
        break;
      case ASTConstantsAssignmentExpressions.MINUSEQUALS:
        getPrinter().print(("-="));
        break;
      case ASTConstantsAssignmentExpressions.STAREQUALS:
        getPrinter().print(("*="));
        break;
      case ASTConstantsAssignmentExpressions.SLASHEQUALS:
        getPrinter().print(("/="));
        break;
      case ASTConstantsAssignmentExpressions.ANDEQUALS:
        getPrinter().print(("&="));
        break;
      case ASTConstantsAssignmentExpressions.PIPEEQUALS:
        getPrinter().print(("|="));
        break;
      case ASTConstantsAssignmentExpressions.ROOFEQUALS:
        getPrinter().print(("^="));
        break;
      case ASTConstantsAssignmentExpressions.GTGTEQUALS:
        getPrinter().print((">>="));
        break;
      case ASTConstantsAssignmentExpressions.GTGTGTEQUALS:
        getPrinter().print((">>>="));
        break;
      case ASTConstantsAssignmentExpressions.LTLTEQUALS:
        getPrinter().print(("<<="));
        break;
      case ASTConstantsAssignmentExpressions.PERCENTEQUALS:
        getPrinter().print(("%="));
        break;
      default:
        Log.error("0xA0114 Missing implementation for RegularAssignmentExpression");
    }
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
