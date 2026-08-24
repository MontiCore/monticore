// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.commonexpressions.types3.util;

import de.monticore.expressions.commonexpressions._ast.ASTArrayAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.commonexpressions._ast.ASTStaticFieldAccessExpression;
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
    return switch (expression) {
      case ASTArrayAccessExpression e -> true;
      case ASTFieldAccessExpression e -> true;
      case ASTQualifiedNameExpression e -> true;
      case ASTNameExpression e -> true;
      case ASTStaticFieldAccessExpression e -> true;
      default -> false;
    };
  }

  // static delegate

  public static void init() {
    Log.trace("init CommonExpressionsLValueRelations", "TypeCheck setup");
    setDelegate(new CommonExpressionsLValueRelations());
  }

}
