/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTExpressionStatementCoCo;
import de.monticore.types.check.TypeCalculator;

public class ExpressionStatementIsValid implements MCCommonStatementsASTExpressionStatementCoCo {

  protected TypeCalculator typeCheck;

  public ExpressionStatementIsValid(TypeCalculator typeCheck) {
    Preconditions.checkNotNull(typeCheck);

    this.typeCheck = typeCheck;
  }

  protected TypeCalculator getTypeCheck() {
    return this.typeCheck;
  }

  @Override
  public void check(ASTExpressionStatement statement) {
    Preconditions.checkNotNull(statement);
    this.getTypeCheck().typeOf(statement.getExpression());
  }
}
