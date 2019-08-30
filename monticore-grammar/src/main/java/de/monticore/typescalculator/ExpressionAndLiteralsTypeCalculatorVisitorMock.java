/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;

import java.util.HashMap;
import java.util.Map;

public class ExpressionAndLiteralsTypeCalculatorVisitorMock implements IExpressionAndLiteralsTypeCalculatorVisitor {

  Map<ASTExpression, SymTypeExpression> lookUp = new HashMap<>();

  @Override
  public SymTypeExpression calculateType(ASTExpression e) {

    if(lookUp.containsKey(e)) {
      return lookUp.get(e);
    }
    return null;
  }

  @Override
  public Map<ASTNode, SymTypeExpression> getTypes() {
    return null;
  }

  @Override
  public void setScope(IExpressionsBasisScope scope) {

  }

  public void addLookUp(ASTExpression e, SymTypeExpression type) {
    lookUp.put(e,type);
  }


}

