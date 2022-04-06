/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTEnhancedForControlCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

public class ForEachIsValid implements MCCommonStatementsASTEnhancedForControlCoCo {

  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0907 ";

  public static final String ERROR_MSG_FORMAT = "For-each loop expression must be an array of subtype of list.";

  public ForEachIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  @Override
  public void check(ASTEnhancedForControl node) {

    SymTypeExpression expression = typeCheck.typeOf(node.getExpression());
    SymTypeExpression arrays = SymTypeExpressionFactory.createTypeObject("java.util.Arrays", node.getEnclosingScope());
    SymTypeExpression lists = SymTypeExpressionFactory.createTypeObject("java.lang.Iterable", node.getEnclosingScope());

    if (!TypeCheck.isSubtypeOf(expression, arrays)) {
      if (!TypeCheck.isSubtypeOf(expression, lists)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      }
    }
  }
}