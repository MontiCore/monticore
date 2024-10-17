/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTSwitchStatementCoCo;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SwitchStatementValid implements MCCommonStatementsASTSwitchStatementCoCo {

  TypeCalculator typeCheck;

  public static final String ERROR_CODE = "0xA0917";

  public static final String ERROR_MSG_FORMAT =
      "Switch expression in the switch-statement must be " +
          "char, byte, short, int, Character, Byte, Short, " +
          "Integer, or an enum type.";

  public SwitchStatementValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  //JLS3 14.11
  @Override
  public void check(ASTSwitchStatement node) {

    SymTypeExpression result = typeCheck.typeOf(node.getExpression());

    if (!(typeCheck.isChar(result) || typeCheck.isByte(result)
        || typeCheck.isShort(result) || typeCheck.isInt(result)
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