/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTSwitchStatementCoCo;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SwitchStatementValid implements MCCommonStatementsASTSwitchStatementCoCo {

  @Deprecated
  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0917";

  public static final String ERROR_MSG_FORMAT =
    "Switch expression in the switch-statement must be " +
      "char, byte, short, int, Character, Byte, Short, " +
      "Integer, or an enum type.";

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public SwitchStatementValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public SwitchStatementValid() {
    this(null);
  }

  //JLS3 14.11
  @Override
  public void check(ASTSwitchStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result;

    if (typeCheck != null) {
      // support deprecated behavior
      result = typeCheck.typeOf(node.getExpression());
    } else {
      result = TypeCheck3.typeOf(node.getExpression());
    }

    if (!(SymTypeRelations.isChar(result) || SymTypeRelations.isByte(result)
      || SymTypeRelations.isShort(result) || SymTypeRelations.isInt(result)
      || isEnumMember(result))) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, node.get_SourcePositionStart());
    }

  }

  public boolean isEnumMember(SymTypeExpression ste) {
    if (ste.hasTypeInfo()) {
      if (OOSymbolsMill.typeDispatcher().isOOSymbolsOOType(ste.getTypeInfo())) {
        return OOSymbolsMill.typeDispatcher().asOOSymbolsOOType(ste.getTypeInfo()).isIsEnum();
      }
    }
    return false;
  }
}