/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest.DeriveSymTypeFromTypeCheckTest;
import mc.typechecktest.SynthesizeSymTypeFromTypeCheckTest;
import mc.typechecktest._ast.ASTTCVarDecl;

public class VariableDeclarationIsCorrect implements TypeCheckTestASTTCVarDeclCoCo {


  @Override
  public void check(ASTTCVarDecl node) {
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromTypeCheckTest(), new DeriveSymTypeFromTypeCheckTest());
    if (node.isPresentExpression()) {
      if (!TypeCheck.compatible(tc.symTypeFromAST(node.getMCType()), tc.typeOf(node.getExpression()))) {
        Log.error("The type and the expression of the variable declaration "
            + node.getName() + " are not compatible");
      }
    }
  }
}
