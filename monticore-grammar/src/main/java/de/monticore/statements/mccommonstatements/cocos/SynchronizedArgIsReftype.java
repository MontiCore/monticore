/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.mcsynchronizedstatements._cocos.MCSynchronizedStatementsASTSynchronizedStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class SynchronizedArgIsReftype implements MCSynchronizedStatementsASTSynchronizedStatementCoCo {
  
  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0918 ";
  
  public static final String ERROR_MSG_FORMAT = "expression in synchronized-statement must have a reference type.";
  
  public SynchronizedArgIsReftype(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTSynchronizedStatement node) {
  
    SymTypeExpression result = typeCheck.typeOf(node.getExpression());
    
    if(result.isGenericType()) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}