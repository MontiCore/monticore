/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTForStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ForConditionHasBooleanType implements MCCommonStatementsASTForStatementCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0906";

  public static final String ERROR_MSG_FORMAT = "Condition of for-loop must be a boolean expression.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ForConditionHasBooleanType(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public ForConditionHasBooleanType() { }

  @Override
  public void check(ASTForStatement node) {
    Preconditions.checkNotNull(node);

    // todo replace with typedispatcher as soon as issues are fixed
    if (!(node.getForControl() instanceof ASTCommonForControl)) {
      return;
    }
    SymTypeExpression result;

    if (typeCheck != null) {
      // support deprecated behavior
      result = typeCheck.typeOf(((ASTCommonForControl) node.getForControl()).getCondition());
    } else {
      result = TypeCheck3.typeOf(((ASTCommonForControl) node.getForControl()).getCondition());
    }

    if (!SymTypeRelations.isBoolean(result)) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}