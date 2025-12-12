/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTWhileStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class WhileConditionHasBooleanType implements MCCommonStatementsASTWhileStatementCoCo {

  public static final String ERROR_CODE = "0xA0919";

  public static final String ERROR_MSG_FORMAT = "Condition in while-statement must be a boolean expression.";

  //JLS3 14.12-1
  @Override
  public void check(ASTWhileStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result = TypeCheck3.typeOf(node.getCondition());

    if (!SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}