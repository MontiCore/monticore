/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.types.check.TypeCalculator;

import java.util.Optional;


public class ExpressionValid implements ExpressionsBasisASTExpressionCoCo {

  // The error message is thrown in typeCheck

  protected Optional<ASTExpression> checkingNode = Optional.empty();

  protected TypeCalculator typeCheck;

  public ExpressionValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  @Override
  public void endVisit(ASTExpression expr) {
    if (checkingNode.isPresent() && checkingNode.get() == expr) {
      checkingNode = Optional.empty();
    }
  }

  @Override
  public void check(ASTExpression expr) {
    if (!checkingNode.isPresent()) {
      // TypeCheck
      typeCheck.typeOf(expr);
      checkingNode = Optional.of(expr);
    }
  }

}
