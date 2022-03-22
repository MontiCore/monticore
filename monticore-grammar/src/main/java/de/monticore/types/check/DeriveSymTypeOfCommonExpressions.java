/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in CommonExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfBSCommonExpressions {

  @Override
  protected List<VariableSymbol> filterModifiersVariables(List<VariableSymbol> variableSymbols) {
    return variableSymbols.stream().filter(f -> f instanceof FieldSymbol).filter(f -> ((FieldSymbol) f).isIsStatic()).collect(Collectors.toList());
  }

  @Override
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols.stream().filter(m -> m instanceof MethodSymbol).filter(m -> ((MethodSymbol) m).isIsStatic()).collect(Collectors.toList());
  }

  @Override
  protected boolean checkModifierType(TypeSymbol typeSymbol) {
    //if the last result is a type and the type is not static then it is not accessible
    if (typeCheckResult.isType()) {
      return typeSymbol instanceof OOTypeSymbol && ((OOTypeSymbol) typeSymbol).isIsStatic();
    }
    return true;
  }

  @Override
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr) {
    return innerResult.getMethodList(expr.getName(), typeCheckResult.isType(), false);
  }

  @Override
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), typeCheckResult.isType(), false);
  }
}
