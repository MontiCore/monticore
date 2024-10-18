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
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class LiteralAssignmentMatchesRegExExpressionCoCo implements
    AssignmentExpressionsASTAssignmentExpressionCoCo {

  @Deprecated
  protected IDerive derive;

  /**
   * @deprecated use the other Constructor
   */
  @Deprecated
  public LiteralAssignmentMatchesRegExExpressionCoCo(IDerive derive) {
    this.derive = derive;
  }

  public LiteralAssignmentMatchesRegExExpressionCoCo() {
  }

  @Override
  public void check(ASTAssignmentExpression node) {
    SymTypeExpression leftType;
    if (derive != null) {
      // support legacy code
      TypeCheckResult leftResult = derive.deriveType(node.getLeft());
      if(!leftResult.isPresentResult()) {
        return;
      }
      leftType = leftResult.getResult();
    } else {
      leftType = TypeCheck3.typeOf(node.getLeft());
      if(leftType.isObscureType()) {
        return;
      }
    }

    IAssignmentExpressionsTypeDispatcher expressionsDispatcher =
        AssignmentExpressionsMill.typeDispatcher();
    IMCCommonLiteralsTypeDispatcher literalDispatcher =
        MCCommonLiteralsMill.typeDispatcher();

      if (leftType.isRegExType() &&
          expressionsDispatcher.isExpressionsBasisASTLiteralExpression(node.getRight())) {
        ASTLiteralExpression literalExpression =
            expressionsDispatcher.asExpressionsBasisASTLiteralExpression(node.getRight());
        if (literalDispatcher.isMCCommonLiteralsASTStringLiteral(literalExpression.getLiteral())) {
          String s = literalDispatcher.asMCCommonLiteralsASTStringLiteral(literalExpression.getLiteral())
              .getSource();
          String regex = leftType.asRegExType().getRegExString();

          if (!s.matches(regex)) {
            Log.error("0xFD724 incompatible String literal \""
                    + s + "\" is assigned to a regex instance "
                    + leftType.printFullName(),
                node.get_SourcePositionStart(),
                node.get_SourcePositionEnd());
          }
        }
      }
  }

}
