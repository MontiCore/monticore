/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.types.check.TypeCalculator;

import java.util.Optional;

/**
 * @deprecated not compatible with target typing,
 * thus, this needs to be replaced with multiple CoCos,
 * which do pass the target type if available.
 * These CoCos have to be created for each non-terminal that contains
 * an Expression.
 * Please refer to the TypeCheck3 documentation and specifically
 * {@link de.monticore.types3.TypeCheck3#typeOf(ASTExpression, de.monticore.types.check.SymTypeExpression)}.
 */
@Deprecated
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
