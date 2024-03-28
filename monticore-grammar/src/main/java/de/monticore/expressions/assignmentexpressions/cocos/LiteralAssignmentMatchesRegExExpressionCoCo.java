/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._util.IAssignmentExpressionsTypeDispatcher;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._util.IMCCommonLiteralsTypeDispatcher;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class LiteralAssignmentMatchesRegExExpressionCoCo implements
    AssignmentExpressionsASTAssignmentExpressionCoCo {

  protected IDerive derive;

  public LiteralAssignmentMatchesRegExExpressionCoCo(IDerive derive) {
    this.derive = derive;
  }

  @Override
  public void check(ASTAssignmentExpression node) {
    TypeCheckResult leftResult = derive.deriveType(node.getLeft());
    IAssignmentExpressionsTypeDispatcher expressionsDispatcher =
        AssignmentExpressionsMill.typeDispatcher();
    IMCCommonLiteralsTypeDispatcher literalDispatcher =
        MCCommonLiteralsMill.typeDispatcher();

    if (leftResult.isPresentResult()) {
      if (leftResult.getResult().isRegExType() &&
          expressionsDispatcher.isExpressionsBasisASTLiteralExpression(node.getRight())) {
        ASTLiteralExpression literalExpression =
            expressionsDispatcher.asExpressionsBasisASTLiteralExpression(node.getRight());
        if (literalDispatcher.isMCCommonLiteralsASTStringLiteral(literalExpression.getLiteral())) {
          String s = literalDispatcher.asMCCommonLiteralsASTStringLiteral(literalExpression.getLiteral())
              .getSource();
          String regex = leftResult.getResult().asRegExType().getRegExString();

          if (!s.matches(regex)) {
            Log.error("0xFD724 incompatible String literal \""
                    + s + "\" is assigned to a regex instance "
                    + leftResult.getResult().printFullName(),
                node.get_SourcePositionStart(),
                node.get_SourcePositionEnd());
          }
        }
      }
    }
  }

}
