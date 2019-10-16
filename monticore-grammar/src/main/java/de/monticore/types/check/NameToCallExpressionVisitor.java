package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;

// TODO: BEschreiben
// TODO 4Rel:
// this class is used by DeriveSymTypeOfCommonExpressions
// to derive a name for ASTCallExpression
// However, call expressions not necessarily have a name. Unclear, what the purpose is

// Wenn es darum geht, bei einem call der form  "foo(x,y)" das foo zu extrahieren, lässt sich das
// auch regeln, ohne im Baum Veränderungen vorzunehmen, zB durch lokales speichern und übergabe
// des Ergebnisses and en Aufrufer

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
