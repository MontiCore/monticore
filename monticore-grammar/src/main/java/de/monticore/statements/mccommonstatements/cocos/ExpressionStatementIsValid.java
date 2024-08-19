/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTExpressionStatementCoCo;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.TypeCheck3;

public class ExpressionStatementIsValid implements MCCommonStatementsASTExpressionStatementCoCo {

  @Deprecated
  protected TypeCalculator typeCheck;

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ExpressionStatementIsValid(TypeCalculator typeCheck) {
    Preconditions.checkNotNull(typeCheck);

    this.typeCheck = typeCheck;
  }

  public ExpressionStatementIsValid() {
  }

  @Deprecated
  protected TypeCalculator getTypeCheck() {
    return this.typeCheck;
  }

  @Override
  public void check(ASTExpressionStatement statement) {
    Preconditions.checkNotNull(statement);
    if(typeCheck != null) {
    // support deprecated behavior
    this.getTypeCheck().typeOf(statement.getExpression());
    } else {
    TypeCheck3.typeOf(statement.getExpression());
    }
  }
}
