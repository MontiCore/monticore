/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTDoWhileStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

public class DoWhileConditionHasBooleanType implements MCCommonStatementsASTDoWhileStatementCoCo {
  
  TypeCalculator typeCheck;
  
  public static final String ERROR_CODE = "0xA0905";
  
  public static final String ERROR_MSG_FORMAT = "Condition in do-statement must be a boolean expression.";
  
  public DoWhileConditionHasBooleanType(TypeCalculator typeCheck){
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTDoWhileStatement node) {
  
    SymTypeExpression result = typeCheck.typeOf(node.getCondition());
  
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
    
  }
}