/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsASTThrowStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class ThrowIsValid implements MCExceptionStatementsASTThrowStatementCoCo{
  
  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0918";
  
  public static final String ERROR_MSG_FORMAT = " exception in throw-statement must be Throwable or subtype of it.";
  
  public ThrowIsValid(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  //JLS3 14.18-1
  @Override
  public void check(ASTThrowStatement node) {
    
    SymTypeExpression throwable = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", node.getEnclosingScope());
    
    if(!TypeCheck.isSubtypeOf(typeCheck.typeOf(node.getExpression()),throwable)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
    
  }
}