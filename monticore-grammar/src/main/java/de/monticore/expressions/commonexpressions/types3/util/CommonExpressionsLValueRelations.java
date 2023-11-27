// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.commonexpressions.types3.util;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._util.CommonExpressionsTypeDispatcher;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.types3.util.ILValueRelations;

public class CommonExpressionsLValueRelations implements ILValueRelations {

  /**
   * according to Java Spec 20 4.12.3
   * Note: this is not an "isAssignable"-check,
   * as the variable might be final (s. OOSymbols) and already assigned to.
   */
  @Override
  public boolean isLValue(ASTExpression expression) {
    CommonExpressionsTypeDispatcher dispatcher =
        CommonExpressionsMill.typeDispatcher();
    boolean result;
    if (dispatcher.isASTNameExpression(expression)) {
      result = true;
    }
    else if (dispatcher.isASTFieldAccessExpression(expression)) {
      result = true;
    }
    else if (dispatcher.isASTArrayAccessExpression(expression)) {
      result = true;
    }
    else {
      result = false;
    }
    return result;
  }
}
