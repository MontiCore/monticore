/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._cocos.MCAssertStatementsASTAssertStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class AssertIsValid implements MCAssertStatementsASTAssertStatementCoCo {
  
  public static final String ERROR_CODE = "0xA0901";
  
  public static final String ERROR_MSG_FORMAT = "assert-statement has a wrong type.";
  
  
  TypeCheck typeCheck;
  
  public AssertIsValid(TypeCheck typeCheck){
    
    this.typeCheck = typeCheck;
    
  }
  
  @Override
  public void check(ASTAssertStatement node) {
  
    SymTypeExpression result = typeCheck.typeOf(node.getAssertion());
  
    if(!TypeCheck.isBoolean(result)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.getAssertion().get_SourcePositionStart());
    }
    
    if(node.isPresentMessage()) {
      result = typeCheck.typeOf(node.getMessage());
      if (!TypeCheck.isVoid(result)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.getMessage().get_SourcePositionStart());
      }
    }
  }
}