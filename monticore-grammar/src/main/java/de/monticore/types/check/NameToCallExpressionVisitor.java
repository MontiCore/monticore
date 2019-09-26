/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;

public class NameToCallExpressionVisitor implements CommonExpressionsVisitor {

  private String lastName = null;
  private ASTExpression lastExpression = null;

  public void traverse(ASTCallExpression expr){
    expr.getExpression().accept(this);
    if(lastName!=null) {
      expr.setName(lastName);
      lastName=null;
    }
    if(lastExpression!=null){
      expr.setExpression(lastExpression);
    }
  }

  public void traverse(ASTFieldAccessExpression expr){
    if(lastName==null){
      lastName = expr.getName();
    }
    if(lastExpression == null){
      lastExpression = expr.getExpression();
    }
  }

  public void traverse(ASTNameExpression expr){
    if(lastName==null){
      lastName = expr.getName();
    }
  }

}
