/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
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

    if (leftType.isRegExType() &&
        node.getRight() instanceof ASTLiteralExpression literalExpression &&
        literalExpression.getLiteral() instanceof ASTStringLiteral stringLiteral) {
      String s = stringLiteral.getSource();
      String regex = leftType.asRegExType().getRegExString();

      if (!s.matches(regex)) {
        Log.error(
            "0xFD724 incompatible String literal \"" + s + "\" is assigned to a regex instance "
                + leftType.printFullName(),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
    }
  }

}
