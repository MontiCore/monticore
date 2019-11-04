/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

public interface ITypesCalculator {

  ExpressionsBasisScope scope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();

  Optional<SymTypeExpression> calculateType(ASTExpression ex);

  Optional<SymTypeExpression> calculateType(ASTLiteral lit);

  void init();

  default ExpressionsBasisScope getScope(){
    return scope;
  }
}
