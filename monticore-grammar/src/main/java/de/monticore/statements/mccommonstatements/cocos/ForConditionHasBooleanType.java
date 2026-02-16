/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTCommonForControlCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ForConditionHasBooleanType implements MCCommonStatementsASTCommonForControlCoCo {

  public static final String ERROR_CODE = "0xA0906";

  public static final String ERROR_MSG_FORMAT = "Condition of for-loop must be a boolean expression.";

  @Override
  public void check(ASTCommonForControl node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentCondition()) return;

    SymTypeExpression result = TypeCheck3.typeOf(node.getCondition());

    if (!result.isObscureType() && !SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + " " + ERROR_MSG_FORMAT,
        node.getCondition().get_SourcePositionStart(),
        node.getCondition().get_SourcePositionEnd());
    }
  }
}
