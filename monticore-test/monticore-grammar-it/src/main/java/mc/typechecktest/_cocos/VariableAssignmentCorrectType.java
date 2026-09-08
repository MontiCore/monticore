/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/**
 * @deprecated this is already part of typecheck 3.
 */
@Deprecated
public class VariableAssignmentCorrectType implements AssignmentExpressionsASTAssignmentExpressionCoCo {

  @Override
  public void check(ASTAssignmentExpression node) {
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (rightType.isObscureType()) {
      // the error is already logged by the type derivation
      return;
    }
    if (!SymTypeRelations.isCompatible(leftType, rightType)) {
      Log.error("0xA0456 The types of the assignment are not compatible.");
    }
  }
}
