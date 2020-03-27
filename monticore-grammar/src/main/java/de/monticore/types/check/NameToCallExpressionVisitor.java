/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;

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

public class NameToCallExpressionVisitor implements CommonExpressionsVisitor {

  private String lastName = null;
  private ASTExpression lastExpression = null;

  public void traverse(ASTCallExpression expr){
    // avoid run if Name is already set
    if (expr.getName()==null || expr.getName().isEmpty()) {
      expr.getExpression().accept(this);
      if (lastName != null) {
        expr.setName(lastName);
        lastName = null;
      }
      if (lastExpression != null) {
        expr.setExpression(lastExpression);
      }
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
