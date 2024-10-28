/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsASTCatchClauseCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class CatchIsValid implements MCExceptionStatementsASTCatchClauseCoCo {
  
  public static final String ERROR_CODE = "0xA0903";
  
  public static final String ERROR_MSG_FORMAT = "Parameter in catch-statement has to be throwable or subtype of it.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public CatchIsValid(TypeCalculator typeCheck) { }

  public CatchIsValid() { }

  @Override
  public void check(ASTCatchClause node) {
    Preconditions.checkNotNull(node);
  
    SymTypeExpression throwable = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", node.getEnclosingScope());
  
    for (int i = 0; i < node.getCatchTypeList().sizeMCQualifiedNames(); i++) {
      String name = node.getCatchTypeList().getMCQualifiedName(i).getQName();
  
      SymTypeExpression result = SymTypeExpressionFactory.createTypeObject(name, node.getEnclosingScope());
  
      if (!SymTypeRelations.isSubTypeOf(result, throwable)) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
      }
    }
  }
}