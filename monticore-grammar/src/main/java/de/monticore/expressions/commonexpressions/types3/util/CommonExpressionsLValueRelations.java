// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.commonexpressions.types3.util;

import de.monticore.expressions.commonexpressions._ast.ASTArrayAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.types3.util.LValueRelations;
import de.se_rwth.commons.logging.Log;

public class CommonExpressionsLValueRelations
    extends LValueRelations {

  /**
   * according to Java Spec 20 4.12.3
   * Note: this is not an "isAssignable"-check,
   * as the variable might be final (s. OOSymbols) and already assigned to.
   */
  @Override
  protected boolean _isLValue(ASTExpression expression) {
    boolean result;
    if (expression instanceof ASTNameExpression) {
      result = true;
    }
    else if (expression instanceof ASTFieldAccessExpression) {
      result = true;
    }
    else if (expression instanceof ASTArrayAccessExpression) {
      result = true;
    }
    else {
      result = false;
    }
    return result;
  }

  // static delegate

  public static void init() {
    Log.trace("init CommonExpressionsLValueRelations", "TypeCheck setup");
    setDelegate(new CommonExpressionsLValueRelations());
  }

}
