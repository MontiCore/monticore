/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTIfStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

public class IfConditionHasBooleanType implements MCCommonStatementsASTIfStatementCoCo {
 
  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0909";
  
  public static final String ERROR_MSG_FORMAT = "condition in if-statement must be a boolean expression.";
  
  public IfConditionHasBooleanType(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  //JLS3 14.9-1
  @Override
  public void check(ASTIfStatement node) {
    SymTypeExpression result = typeCheck.typeOf(node.getCondition());
    
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  }
}