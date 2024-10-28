/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsASTThrowStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ThrowIsValid implements MCExceptionStatementsASTThrowStatementCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0918";

  public static final String ERROR_MSG_FORMAT = " Exception in throw-statement must be Throwable or subtype of it.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ThrowIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public ThrowIsValid() { }

  //JLS3 14.18-1
  @Override
  public void check(ASTThrowStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression throwable = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", node.getEnclosingScope());

    SymTypeExpression expression;

    if (typeCheck != null) {
      // support deprecated behavior
      expression = typeCheck.typeOf(node.getExpression());
    } else {
      expression = TypeCheck3.typeOf(node.getExpression());
    }

    if (!SymTypeRelations.isSubTypeOf(expression, throwable)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }

  }
}