package de.monticore.expressions.streamexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.List;
import java.util.stream.Collectors;

public class ASTToptStreamConstructorExpression
    extends ASTToptStreamConstructorExpressionTOP {

  @Override
  public boolean isToptTimed() {
    return true;
  }

  @Override
  public List<ASTExpression> getExpressionList() {
    return streamExpressionOrAbsents()
        .filter(ASTExpressionOrAbsent::isPresentExpression)
        .map(ASTExpressionOrAbsent::getExpression)
        .collect(Collectors.toList());
  }

}
