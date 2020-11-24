/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

/**
 * The usage of this class is to transform a call expression so that it is easier to resolve in the TypeCheck
 * We take the call expression, set the name of the call expression to the name of the inner name/qualified
 * name expression and set the inner expression to the inner expression of the inner expression
 *
 * This makes it far easier to calculate the type of a call expression
 *
 * Visitor Run is idempotent (use #2430 technique if available)
 * actually this is called in DeriveSymTypeOfCommonExpressions
 */

public class NameToCallExpressionVisitor implements CommonExpressionsVisitor2, CommonExpressionsHandler, ExpressionsBasisVisitor2, ExpressionsBasisHandler {

  protected CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    //the traverser is always a CommonExpressionsTraverser -> cast necessary
    this.traverser = (CommonExpressionsTraverser) traverser;
  }

  private String lastName = null;
  private ASTExpression lastExpression = null;

  @Override
  public void traverse(ASTCallExpression expr){
    // avoid run if Name is already set
    if (expr.getName()==null || expr.getName().isEmpty()) {
      expr.getExpression().accept(getTraverser());
      if (lastName != null) {
        expr.setName(lastName);
        lastName = null;
      }
      if (lastExpression != null) {
        expr.setExpression(lastExpression);
      }
    }
  }

  @Override
  public void traverse(ASTFieldAccessExpression expr){
    if(lastName==null){
      lastName = expr.getName();
    }
    if(lastExpression == null){
      lastExpression = expr.getExpression();
    }
  }

  //to implement both ExpressionsBasisVisitor and CommonExpressionsVisitor, these methods must be overridden

  @Override
  public void traverse(ASTNameExpression expr){
    if(lastName==null){
      lastName = expr.getName();
    }
  }

  @Override
  public void visit(IScope node) {

  }

  @Override
  public void endVisit(IScope node) {

  }

  @Override
  public void visit(ASTNode node) {

  }

  @Override
  public void endVisit(ASTNode node) {

  }

  @Override
  public void visit(ISymbol node) {

  }

  @Override
  public void endVisit(ISymbol node) {

  }


}
