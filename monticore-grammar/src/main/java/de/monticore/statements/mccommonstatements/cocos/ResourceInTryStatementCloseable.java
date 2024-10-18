/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mcexceptionstatements._ast.ASTTryLocalVariableDeclaration;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryStatement3;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsASTTryStatement3CoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

public class ResourceInTryStatementCloseable implements MCExceptionStatementsASTTryStatement3CoCo {
  
  TypeCalculator typeCheck;
  
  public static final String ERROR_CODE = "0xA0920";
  
  public static final String ERROR_MSG_FORMAT = " Resource in try-statement must be closeable or subtype of it.";
  
  public ResourceInTryStatementCloseable(TypeCalculator typeCheck){
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTTryStatement3 node) {
    
    SymTypeExpression closeable = SymTypeExpressionFactory.createTypeObject("java.io.Closeable", node.getEnclosingScope());
  
    for (ASTTryLocalVariableDeclaration dec: node.getTryLocalVariableDeclarationList()){
      if (!TypeCheck.isSubtypeOf(typeCheck.typeOf(dec.getExpression()), closeable)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      }
    }
  }
}