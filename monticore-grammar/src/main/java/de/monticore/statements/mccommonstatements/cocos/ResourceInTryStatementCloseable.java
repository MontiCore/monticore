/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryLocalVariableDeclaration;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryStatement3;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsASTTryStatement3CoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class ResourceInTryStatementCloseable implements MCExceptionStatementsASTTryStatement3CoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0920";

  public static final String ERROR_MSG_FORMAT = " Resource in try-statement must be closeable or subtype of it.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ResourceInTryStatementCloseable(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public ResourceInTryStatementCloseable() { }

  @Override
  public void check(ASTTryStatement3 node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression closeable = SymTypeExpressionFactory.createTypeObject("java.io.Closeable", node.getEnclosingScope());

    for (ASTTryLocalVariableDeclaration dec : node.getTryLocalVariableDeclarationList()) {

      SymTypeExpression expression;

      if (typeCheck != null) {
        // support deprecated behavior
        expression = typeCheck.typeOf(dec.getExpression());
      } else {
        expression = TypeCheck3.typeOf(dec.getExpression());
      }

      if (!SymTypeRelations.isSubTypeOf(expression, closeable)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      }
    }
  }
}