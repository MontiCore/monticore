/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTSwitchStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class SwitchStatementValid implements MCCommonStatementsASTSwitchStatementCoCo {

  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0917";
  
  public static final String ERROR_MSG_FORMAT = "switch expression in the switch-statement must be char, byte, short, int, Character," +
    "Byte, Short, Integer, or an enum type.";
  
  public SwitchStatementValid(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  //JLS3 14.11
  @Override
  public void check(ASTSwitchStatement node) {
  
    SymTypeExpression result = typeCheck.typeOf(node.getExpression());
    
    if(!(TypeCheck.isChar(result)||TypeCheck.isByte(result)||TypeCheck.isShort(result)||TypeCheck.isInt(result))){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
    
  }
}