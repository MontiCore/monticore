/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTEnhancedForControlCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;

public class ForEachIsValid implements MCCommonStatementsASTEnhancedForControlCoCo {
  
  TypeCheck typeCheck;
  
  public static final String ERROR_CODE = "0xA0907 ";
  
  public static final String ERROR_MSG_FORMAT = "for-each loop is wrong.";
  
  public ForEachIsValid(TypeCheck typeCheck){
    this.typeCheck = typeCheck;
  }
  
  @Override
  public void check(ASTEnhancedForControl node) {
    
    SymTypeExpression expression = typeCheck.typeOf(node.getExpression());
    ASTFormalParameter parameter = node.getFormalParameter();
  
    SymTypeExpression typeOfParameter = typeCheck.symTypeFromAST(parameter.getMCType());
  
    SymTypeExpression arrays  = SymTypeExpressionFactory.createTypeObject("java.util.Arrays", node.getEnclosingScope());
    SymTypeExpression lists = SymTypeExpressionFactory.createTypeObject("java.lang.Iterable", node.getEnclosingScope());
    
    if(!TypeCheck.isSubtypeOf(expression, arrays)){
      if(!TypeCheck.isSubtypeOf(expression, lists)){
        Log.error(ERROR_CODE+ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      } else if (!TypeCheck.isSubtypeOf(typeOfParameter, expression)){
        Log.error(ERROR_CODE+ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      }
    } else if (!TypeCheck.isSubtypeOf(typeOfParameter, expression)){
      Log.error(ERROR_CODE+ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }
  
  }
}