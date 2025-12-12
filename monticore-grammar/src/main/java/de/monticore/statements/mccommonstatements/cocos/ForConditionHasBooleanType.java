/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTForStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ForConditionHasBooleanType implements MCCommonStatementsASTForStatementCoCo {

  public static final String ERROR_CODE = "0xA0906";

  public static final String ERROR_MSG_FORMAT = "Condition of for-loop must be a boolean expression.";

  @Override
  public void check(ASTForStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result = TypeCheck3.typeOf(((ASTCommonForControl) node.getForControl()).getCondition());

    if (!SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}