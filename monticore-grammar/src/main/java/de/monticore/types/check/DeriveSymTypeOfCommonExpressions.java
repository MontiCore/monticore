/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in CommonExpressions.
 * It is the OO extension of the class {@link de.monticore.types.check.DeriveSymTypeOfBSCommonExpressions} and adds OO
 * functionalities like modifiers to the derivation of a SymTypeExpression.
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 * @deprecated Use {@link de.monticore.types3.TypeCheck3} instead.
 */
@Deprecated(forRemoval = true)
public class DeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfBSCommonExpressions {

  @Override
  protected List<VariableSymbol> filterModifiersVariables(List<VariableSymbol> variableSymbols) {
    return variableSymbols.stream().filter(f -> f instanceof FieldSymbol ? ((FieldSymbol) f).isIsStatic() : false).collect(Collectors.toList());
  }

  @Override
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols.stream().filter(m -> m instanceof MethodSymbol ? ((MethodSymbol) m).isIsStatic() : true).collect(Collectors.toList());
  }

  @Override
  protected boolean checkModifierType(TypeSymbol typeSymbol) {
    //if the last result is a type and the type is not static then it is not accessible
    if (getTypeCheckResult().isType()) {
      return typeSymbol instanceof OOTypeSymbol ? ((OOTypeSymbol) typeSymbol).isIsStatic() : true;
    }
    return true;
  }

  @Override
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr, String name) {
    return innerResult.getMethodList(name, getTypeCheckResult().isType(), false, AccessModifier.ALL_INCLUSION);
  }

  @Override
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), getTypeCheckResult().isType(), false, AccessModifier.ALL_INCLUSION);
  }
}
