// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecSuffixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncPrefixExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncSuffixExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.types3.util.ILValueRelations;
import de.se_rwth.commons.logging.Log;

public class AssignmentExpressionsOnlyAssignToLValuesCoCo implements
    AssignmentExpressionsASTAssignmentExpressionCoCo,
    AssignmentExpressionsASTDecPrefixExpressionCoCo,
    AssignmentExpressionsASTDecSuffixExpressionCoCo,
    AssignmentExpressionsASTIncPrefixExpressionCoCo,
    AssignmentExpressionsASTIncSuffixExpressionCoCo {

  /**
   * the lvalue relations are directly dependent
   * of the other language components,
   * as such there is no default
   */
  ILValueRelations lValueRelations = null;

  public AssignmentExpressionsOnlyAssignToLValuesCoCo(
      ILValueRelations lValueRelations) {
    this.lValueRelations = lValueRelations;
  }

  protected ILValueRelations getLValueRelations() {
    return lValueRelations;
  }

  @Override
  public void check(ASTAssignmentExpression node) {
    failIfNoLValue(node.getLeft());
  }

  @Override
  public void check(ASTDecPrefixExpression node) {
    failIfNoLValue(node.getExpression());
  }

  @Override
  public void check(ASTDecSuffixExpression node) {
    failIfNoLValue(node.getExpression());
  }

  @Override
  public void check(ASTIncPrefixExpression node) {
    failIfNoLValue(node.getExpression());
  }

  @Override
  public void check(ASTIncSuffixExpression node) {
    failIfNoLValue(node.getExpression());
  }

  // Helper

  protected void failIfNoLValue(ASTExpression expr) {
    if (!getLValueRelations().isLValue(expr)) {
      Log.error("0xFDD47 expression is not a lvalue "
              + "but being assigned to",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
  }

}
