/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;

public class ASTCallExpression extends ASTCallExpressionTOP {

  private EMethodSymbol eMethodSymbol;

  protected ASTCallExpression() {
  }


  protected ASTCallExpression(ASTExpression expression, ASTArguments arguments) {
    setExpression(expression);
    setArguments(arguments);
  }

  public EMethodSymbol geteMethodSymbol() {
    return eMethodSymbol;
  }

  public void seteMethodSymbol(EMethodSymbol eMethodSymbol) {
    this.eMethodSymbol = eMethodSymbol;
  }



}
