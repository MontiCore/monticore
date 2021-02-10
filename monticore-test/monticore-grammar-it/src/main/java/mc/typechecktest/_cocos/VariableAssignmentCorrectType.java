/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest.DeriveSymTypeFromTypeCheckTest;
import mc.typechecktest.SynthesizeSymTypeFromTypeCheckTest;

public class VariableAssignmentCorrectType implements AssignmentExpressionsASTAssignmentExpressionCoCo {
  @Override
  public void check(ASTAssignmentExpression node) {
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromTypeCheckTest(), new DeriveSymTypeFromTypeCheckTest());
    if(!TypeCheck.compatible(tc.typeOf(node.getLeft()), tc.typeOf(node.getRight()))){
      Log.error("The types of the assignment are not compatible.");
    }
  }
}
