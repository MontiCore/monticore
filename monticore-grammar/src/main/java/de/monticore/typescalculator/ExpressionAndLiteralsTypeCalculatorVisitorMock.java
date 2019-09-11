/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.SymTypeExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExpressionAndLiteralsTypeCalculatorVisitorMock implements IExpressionAndLiteralsTypeCalculatorVisitor {

  Map<ASTExpression, SymTypeExpression> lookUp = new HashMap<>();

  @Override
  public Optional<SymTypeExpression> calculateType(ASTExpression e) {

    if(lookUp.containsKey(e)) {
      return Optional.of(lookUp.get(e));
    }
    return Optional.empty();
  }

  @Override
  public void setScope(IExpressionsBasisScope scope) {

  }

  public void addLookUp(ASTExpression e, SymTypeExpression type) {
    lookUp.put(e,type);
  }


}

