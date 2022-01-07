/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTForControl;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTForControlCoCo;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTForStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class ForConditionHasBooleanType implements MCCommonStatementsASTForStatementCoCo {
  
  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0906";
  
  public static final String ERROR_MSG_FORMAT = "Condition of for-loop must be a boolean expression.";
  
  public ForConditionHasBooleanType(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTForStatement node) {
    
    SymTypeExpression result = typeCheck.typeOf(((ASTCommonForControl)node.getForControl()).getCondition());
    
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
    
  }
}