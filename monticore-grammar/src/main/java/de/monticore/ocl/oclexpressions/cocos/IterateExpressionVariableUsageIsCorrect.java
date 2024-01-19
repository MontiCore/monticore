// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.oclexpressions.cocos;

import de.monticore.ocl.oclexpressions._ast.ASTIterateExpression;
import de.monticore.ocl.oclexpressions._cocos.OCLExpressionsASTIterateExpressionCoCo;
import de.se_rwth.commons.logging.Log;

public class IterateExpressionVariableUsageIsCorrect
    implements OCLExpressionsASTIterateExpressionCoCo {

  @Override
  public void check(ASTIterateExpression node) {
    String variableName = node.getInit().getName();
    if (!node.getName().equals(variableName)) {
      Log.error(
          String.format(
              "0x0C1CC variable declared in IterateExpression must be used as accumulator variable"),
          node.get_SourcePositionStart());
    }
  }
}
