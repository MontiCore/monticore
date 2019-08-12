/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.Map;

public interface IExpressionAndLiteralsTypeCalculatorVisitor {
  public ASTMCType calculateType(ASTExpression e);

  Map<ASTNode, MCTypeSymbol> getTypes();

  void setScope(ExpressionsBasisScope scope);
}
