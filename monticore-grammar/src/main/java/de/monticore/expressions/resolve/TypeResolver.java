package de.monticore.expressions.resolve;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class TypeResolver {

  public ASTMCType resolve(ASTExpression expression) {

    if( expression instanceof ASTCallExpression) {

      ASTCallExpression callE = (ASTCallExpression) expression;

      EMethodSymbol methodSymbol = callE.getNameSymbol();


//      methodSymbol.

    }


    return null;
  }

}
