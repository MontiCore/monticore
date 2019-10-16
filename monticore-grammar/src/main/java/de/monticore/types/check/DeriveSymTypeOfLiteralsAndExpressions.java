/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

public class DeriveSymTypeOfLiteralsAndExpressions implements ITypesCalculator {


  public Optional<SymTypeExpression> calculateType(ASTExpression ex){
    return Optional.empty();
  }

  public Optional<SymTypeExpression> calculateType(ASTLiteral lit){
    return Optional.empty();
  }

  @Override
  public void init() {

  }
}
