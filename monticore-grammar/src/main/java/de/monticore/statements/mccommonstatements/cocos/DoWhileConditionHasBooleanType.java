/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTDoWhileStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class DoWhileConditionHasBooleanType implements MCCommonStatementsASTDoWhileStatementCoCo {

  public static final String ERROR_CODE = "0xA0905";

  public static final String ERROR_MSG_FORMAT = "Condition in do-statement must be a boolean expression.";

  @Override
  public void check(ASTDoWhileStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result = TypeCheck3.typeOf(node.getCondition());

    if (!SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}