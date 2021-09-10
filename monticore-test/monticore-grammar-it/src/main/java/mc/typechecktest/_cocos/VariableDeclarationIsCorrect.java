/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest._ast.ASTTCVarDecl;

public class VariableDeclarationIsCorrect implements TypeCheckTestASTTCVarDeclCoCo {

  protected TypeCheck tc;

  public VariableDeclarationIsCorrect(TypeCheck tc){
    this.tc = tc;
  }


  @Override
  public void check(ASTTCVarDecl node) {
    if (node.isPresentExpression() && !TypeCheck.compatible(tc.symTypeFromAST(node.getMCType()), tc.typeOf(node.getExpression()))) {
      Log.error("0xA0457 The type and the expression of the variable declaration "
          + node.getName() + " are not compatible");
    }
  }
}
