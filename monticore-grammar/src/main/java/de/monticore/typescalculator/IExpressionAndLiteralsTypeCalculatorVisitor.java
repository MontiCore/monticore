/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;

import java.util.Map;

public interface IExpressionAndLiteralsTypeCalculatorVisitor {
  public SymTypeExpression calculateType(ASTExpression e);

  Map<ASTNode, SymTypeExpression> getTypes();

  void setScope(IExpressionsBasisScope scope);
}
