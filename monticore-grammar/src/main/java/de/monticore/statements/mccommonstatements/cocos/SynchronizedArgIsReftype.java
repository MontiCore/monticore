/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.mcsynchronizedstatements._cocos.MCSynchronizedStatementsASTSynchronizedStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SynchronizedArgIsReftype implements MCSynchronizedStatementsASTSynchronizedStatementCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0918 ";

  public static final String ERROR_MSG_FORMAT = "Expression in synchronized-statement must have a reference type.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public SynchronizedArgIsReftype(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public SynchronizedArgIsReftype() { }

  @Override
  public void check(ASTSynchronizedStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result;

    if (typeCheck != null) {
      // support deprecated behavior
      result = typeCheck.typeOf(node.getExpression());
    } else {
      result = TypeCheck3.typeOf(node.getExpression());
    }

    if (!result.isObjectType()) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}