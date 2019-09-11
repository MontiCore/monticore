package de.monticore.types2;

import de.monticore.combineliteralandexpressionsbasis._visitor.CombineLiteralAndExpressionsBasisVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

public class DeriveSymTypeOfLiteralsAndExpressions implements CombineLiteralAndExpressionsBasisVisitor, ITypesCalculator {

  CombineLiteralAndExpressionsBasisVisitor realThis = this;

  @Override
  public CombineLiteralAndExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CombineLiteralAndExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

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
