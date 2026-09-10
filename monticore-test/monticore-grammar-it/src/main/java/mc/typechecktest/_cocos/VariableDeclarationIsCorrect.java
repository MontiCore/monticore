/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest._ast.ASTTCVarDecl;

public class VariableDeclarationIsCorrect implements TypeCheckTestASTTCVarDeclCoCo {

  @Override
  public void check(ASTTCVarDecl node) {
    if (node.isPresentExpression()) {
      SymTypeExpression declType = TypeCheck3.symTypeFromAST(node.getMCType());
      SymTypeExpression initType = TypeCheck3.typeOf(node.getExpression());
      if (initType.isObscureType()) {
        // the error is already logged by the type derivation
        return;
      }
      if (!SymTypeRelations.isCompatible(declType, initType)) {
        Log.error("0xA0457 The type and the expression of the variable declaration "
            + node.getName() + " are not compatible");
      }
    }
  }
}
