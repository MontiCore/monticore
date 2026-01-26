/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTExpressionStatementCoCo;
import de.monticore.types3.TypeCheck3;

public class ExpressionStatementIsValid implements MCCommonStatementsASTExpressionStatementCoCo {

  @Override
  public void check(ASTExpressionStatement node) {
    Preconditions.checkNotNull(node);

    TypeCheck3.typeOf(node.getExpression());
  }
}
