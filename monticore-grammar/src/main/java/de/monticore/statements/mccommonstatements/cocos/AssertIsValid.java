/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._cocos.MCAssertStatementsASTAssertStatementCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class AssertIsValid implements MCAssertStatementsASTAssertStatementCoCo {
  
  public static final String ERROR_CODE = "0xA0901";
  
  public static final String ERROR_MSG_FORMAT = "Assert-statement must be of boolean type.";
  
  public static final String ERROR_CODE_2 = "0xA0902";
  
  public static final String ERROR_MSG_FORMAT_2 = "Assert-statement must not be of void type.";

  TypeCalculator typeCheck;

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public AssertIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  @Override
  public void check(ASTAssertStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression assertion;

    if (typeCheck != null) {
      // support deprecated behavior
      assertion = typeCheck.typeOf(node.getAssertion());
    } else {
      assertion = TypeCheck3.typeOf(node.getAssertion());
    }
  
    if(!SymTypeRelations.isBoolean(assertion)){
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.getAssertion().get_SourcePositionStart());
    }

    if(node.isPresentMessage()) {

      SymTypeExpression message;

      if (typeCheck != null) {
        // support deprecated behavior
        message = typeCheck.typeOf(node.getMessage());
      } else {
        message = TypeCheck3.typeOf(node.getMessage());
      }

      if (message.isVoidType()) {
        Log.error(ERROR_CODE_2 + ERROR_MSG_FORMAT_2, node.getMessage().get_SourcePositionStart());
      }
    }
  }
}