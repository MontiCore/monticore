/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types2.SymTypeExpression;

import java.util.Map;
import java.util.Optional;

public interface IExpressionAndLiteralsTypeCalculatorVisitor {
  public Optional<SymTypeExpression> calculateType(ASTExpression e);

  void setScope(IExpressionsBasisScope scope);
}
