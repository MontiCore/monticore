/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;

import java.util.HashMap;
import java.util.Map;

public class ExpressionAndLiteralsTypeCalculatorVisitorMock implements IExpressionAndLiteralsTypeCalculatorVisitor {

  Map<ASTExpression,TypeExpression> lookUp = new HashMap<>();

  @Override
  public TypeExpression calculateType(ASTExpression e) {

    if(lookUp.containsKey(e)) {
      return lookUp.get(e);
    }
    return null;
  }

  @Override
  public Map<ASTNode, TypeExpression> getTypes() {
    return null;
  }

  @Override
  public void setScope(IExpressionsBasisScope scope) {

  }

  public void addLookUp(ASTExpression e, TypeExpression type) {
    lookUp.put(e,type);
  }


}

