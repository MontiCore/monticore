/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTEnhancedForControlCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ForEachIsValid implements MCCommonStatementsASTEnhancedForControlCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0907";

  public static final String ERROR_MSG_FORMAT =
      "For-each loop expression must be an array of subtype of list."
          + " Instead, the type is ";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ForEachIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public ForEachIsValid() {
  }

  @Override
  public void check(ASTEnhancedForControl node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression expression = TypeCheck3.typeOf(node.getExpression());

    SymTypeExpression arrays = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.util.Arrays", node.getEnclosingScope());
    SymTypeExpression lists = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Iterable", node.getEnclosingScope());

    if (!expression.isObscureType()
      && !SymTypeRelations.isSubTypeOf(expression, arrays)
      && !SymTypeRelations.isSubTypeOf(expression, lists)) {
      Log.error(ERROR_CODE + " " + ERROR_MSG_FORMAT
              + expression.printFullName(),
          node.get_SourcePositionStart()
      );
    }
  }
}
