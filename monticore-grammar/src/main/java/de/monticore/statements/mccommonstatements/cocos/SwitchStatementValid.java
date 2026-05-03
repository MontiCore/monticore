/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.ASTConstantExpressionSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTEnumConstantSwitchLabel;
import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTSwitchStatementCoCo;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SwitchStatementValid implements MCCommonStatementsASTSwitchStatementCoCo {

  public static final String ERROR_CODE = "0xA0917";

  public static final String ERROR_MSG_FORMAT =
    "Switch expression in the switch-statement must be " +
      "char, byte, short, int, Character, Byte, Short, " +
      "Integer, or an enum type.";

  public static final String CASE_ERROR_CODE = "0xA0925";

  public static final String CASE_ERROR_MSG_FORMAT =
    " Case value of type '%s' is not compatible with switch expression type '%s'.";

  //JLS3 14.11
  @Override
  public void check(ASTSwitchStatement node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression result = TypeCheck3.typeOf(node.getExpression());

    if (!result.isObscureType() && !isSwitchExpressionTypeValid(result)) {
      Log.error(ERROR_CODE + " " + ERROR_MSG_FORMAT, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }

    MCCommonStatementsTraverser traverser = MCCommonStatementsMill.traverser();
    SwitchLabelVisitor labelVisitor = new SwitchLabelVisitor(result);
    traverser.add4MCCommonStatements(labelVisitor);

    node.getSwitchBlockStatementGroupList()
        .forEach(group -> group.getSwitchLabelList()
            .forEach(label -> label.accept(traverser)));
    node.getSwitchLabelList()
        .forEach(label -> label.accept(traverser));
  }

  protected class SwitchLabelVisitor implements MCCommonStatementsVisitor2 {
    protected final SymTypeExpression switchType;

    public SwitchLabelVisitor(SymTypeExpression switchType) {
      this.switchType = switchType;
    }

    @Override
    public void visit(ASTConstantExpressionSwitchLabel node) {
      if (!isSwitchExpressionTypeValid(switchType)) {
        return;
      }

      if (isEnumMember(switchType) && node.getConstant() instanceof ASTNameExpression) {
        String enumConstant = ((ASTNameExpression) node.getConstant()).getName();
        if (isEnumConstantOfSwitchType(enumConstant, switchType)) {
          return;
        }
        Log.error(
          CASE_ERROR_CODE + " " + String.format(CASE_ERROR_MSG_FORMAT, enumConstant, switchType.printFullName()),
          node.getConstant().get_SourcePositionStart(),
          node.getConstant().get_SourcePositionEnd()
        );
        return;
      }

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
    public void visit(ASTEnumConstantSwitchLabel node) {
      if (!isSwitchExpressionTypeValid(switchType)) {
        return;
      }

      if (!isEnumConstantOfSwitchType(node.getEnumConstant(), switchType)) {
        Log.error(
          CASE_ERROR_CODE + " " + String.format(CASE_ERROR_MSG_FORMAT, node.getEnumConstant(), switchType.printFullName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    }
  }

  protected boolean isSwitchExpressionTypeValid(SymTypeExpression type) {
    return SymTypeRelations.isChar(type)
      || SymTypeRelations.isByte(type)
      || SymTypeRelations.isShort(type)
      || SymTypeRelations.isInt(type)
      || isEnumMember(type);
  }

  protected boolean isEnumConstantOfSwitchType(String enumConstant, SymTypeExpression switchType) {
    if (!isEnumMember(switchType)) {
      return false;
    }

    OOTypeSymbol enumType = OOSymbolsMill.typeDispatcher()
      .asOOSymbolsOOType(switchType.getTypeInfo());
    return enumType.getFieldList(enumConstant).stream()
      .anyMatch(field -> SymTypeRelations.isCompatible(switchType, field.getType()));
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