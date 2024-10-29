/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTIfStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class IfConditionHasBooleanType implements MCCommonStatementsASTIfStatementCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0909";

  public static final String ERROR_MSG_FORMAT = " Condition in if-statement must be a boolean expression.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public IfConditionHasBooleanType(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public IfConditionHasBooleanType() { }

  //JLS3 14.9-1
  @Override
  public void check(ASTIfStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result;

    if (typeCheck != null) {
      // support deprecated behavior
      result = typeCheck.typeOf(node.getCondition());
    } else {
      result = TypeCheck3.typeOf(node.getCondition());
    }

    if (!SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}
