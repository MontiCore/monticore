/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class VariableAssignmentCorrectType implements AssignmentExpressionsASTAssignmentExpressionCoCo {

  protected TypeCheck tc;

  public VariableAssignmentCorrectType(TypeCheck tc){
    this.tc = tc;
  }


  @Override
  public void check(ASTAssignmentExpression node) {
    if(!TypeCheck.compatible(tc.typeOf(node.getLeft()), tc.typeOf(node.getRight()))){
      Log.error("0xA0456 The types of the assignment are not compatible.");
    }
  }
}
