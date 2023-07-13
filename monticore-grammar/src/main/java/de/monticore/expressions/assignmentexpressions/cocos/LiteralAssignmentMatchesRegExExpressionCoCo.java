/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions._util.AssignmentExpressionsTypeDispatcher;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._util.MCCommonLiteralsTypeDispatcher;
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
    AssignmentExpressionsTypeDispatcher expressionsDispatcher =
        AssignmentExpressionsMill.typeDispatcher();
    MCCommonLiteralsTypeDispatcher literalDispatcher =
        MCCommonLiteralsMill.typeDispatcher();

    if (leftResult.isPresentResult()) {
      if (leftResult.getResult().isRegExType() &&
          expressionsDispatcher.isASTLiteralExpression(node.getRight())) {
        ASTLiteralExpression literalExpression =
            expressionsDispatcher.asASTLiteralExpression(node.getRight());
        if (literalDispatcher.isASTStringLiteral(literalExpression.getLiteral())) {
          String s = literalDispatcher.asASTStringLiteral(literalExpression)
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