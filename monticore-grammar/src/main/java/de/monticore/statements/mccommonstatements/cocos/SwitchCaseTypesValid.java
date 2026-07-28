/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTEnumConstantSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTSwitchStatementCoCo;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SwitchCaseTypesValid implements MCCommonStatementsASTSwitchStatementCoCo {

  public static final String CASE_ERROR_CODE = "0xA0925";

  public static final String CASE_ERROR_MSG_FORMAT =
    "Case value of type '%s' is not compatible with switch expression type '%s'.";

  @Override
  public void check(ASTSwitchStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result = TypeCheck3.typeOf(node.getExpression());

    if (result.isObscureType()) {
      return;
    }

    MCCommonStatementsTraverser traverser = MCCommonStatementsMill.traverser();
    SwitchLabelHandler labelHandler = new SwitchLabelHandler(result);
    traverser.setMCCommonStatementsHandler(labelHandler);

    node.getSwitchBlockStatementGroupList()
      .forEach(group -> group.getSwitchLabelList()
        .forEach(label -> label.accept(traverser)));
    node.getSwitchLabelList()
      .forEach(label -> label.accept(traverser));
  }


  protected class SwitchLabelHandler implements MCCommonStatementsHandler {

    protected final SymTypeExpression switchType;
    protected MCCommonStatementsTraverser traverser;

    public SwitchLabelHandler(SymTypeExpression switchType) {
      this.switchType = switchType;
    }

    @Override
    public MCCommonStatementsTraverser getTraverser() {
      return traverser;
    }

    @Override
    public void setTraverser(MCCommonStatementsTraverser traverser) {
      this.traverser = traverser;
    }

    @Override
    public void handle(ASTConstantExpressionSwitchLabel node) {
      SymTypeExpression caseType = TypeCheck3.typeOf(node.getConstant(), switchType);
      if (caseType.isObscureType()) {
        return;
      }

      if (!SymTypeRelations.isCompatible(switchType, caseType)) {
        Log.error(CASE_ERROR_CODE + " " + String.format(CASE_ERROR_MSG_FORMAT, caseType.printFullName(), switchType.printFullName()),
          node.getConstant().get_SourcePositionStart(),
          node.getConstant().get_SourcePositionEnd()
        );
      }
    }

    @Override
    public void handle(ASTEnumConstantSwitchLabel node) {
      if (switchType.getTypeInfo().getVariableList(node.getEnumConstant()).isEmpty()) {
        Log.error(CASE_ERROR_CODE + " " + String.format(CASE_ERROR_MSG_FORMAT, node.getEnumConstant(), switchType.printFullName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    }
  }
}
