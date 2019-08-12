/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.HashMap;
import java.util.Map;

public class ExpressionAndLiteralsTypeCalculatorVisitorMock implements IExpressionAndLiteralsTypeCalculatorVisitor {

  Map<ASTExpression,ASTMCType> lookUp = new HashMap<>();

  @Override
  public ASTMCType calculateType(ASTExpression e) {

    if(lookUp.containsKey(e)) {
      return lookUp.get(e);
    }
    return null;
  }

  @Override
  public Map<ASTNode, MCTypeSymbol> getTypes() {
    return null;
  }

  @Override
  public void setScope(ExpressionsBasisScope scope) {

  }

  public void addLookUp(ASTExpression e, ASTMCType type) {
    lookUp.put(e,type);
  }


}

