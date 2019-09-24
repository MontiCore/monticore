/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeOfObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DummyAdapter implements ITypeSymbolResolvingDelegate, IMethodSymbolResolvingDelegate, IFieldSymbolResolvingDelegate {

  private IExpressionsBasisScope scope;

  private SymTypeExpression a;
  private SymTypeExpression b;

  public DummyAdapter(IExpressionsBasisScope scope){
    this.scope = scope;
  }

  @Override
  public List<MethodSymbol> resolveAdaptedMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    ArrayList<MethodSymbol> list = new ArrayList<>();
    if(symbolName.equals("call")||symbolName.equals("A.B.C.call")) {
      symbolName = "int";
    }
    MethodSymbol sym = ExpressionsBasisSymTabMill.methodSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();
    SymTypeExpression returnType = TypesCalculatorHelper.fromMethodSymbol(sym);
    returnType.setName(symbolName);
    sym.setReturnType(returnType);
    list.add(sym);
    return list;
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    ArrayList<TypeSymbol> list = new ArrayList<>();
    TypeSymbol sym = ExpressionsBasisSymTabMill.typeSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();
    list.add(sym);
    return list;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    ArrayList<FieldSymbol> list = new ArrayList<>();
    if(symbolName.contains("var")){
      symbolName=symbolName.substring(3);
    }
    FieldSymbol sym = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setAccessModifier(modifier).setName(symbolName).setEnclosingScope(scope).build();

    SymTypeExpression type = TypesCalculatorHelper.fromFieldSymbol(sym);
    type.setName(symbolName);
    if(symbolName.equals("A")){
      a = new SymTypeOfObject();
      a.setName("A");
      b = new SymTypeOfObject();
      b.setName("B");

      List<SymTypeExpression> superTypes = new ArrayList<>();
      superTypes.add(a);

      b.setSuperTypes(superTypes);

    }
    if(symbolName.equals("B")){
      ArrayList<SymTypeExpression> superTypes = new ArrayList<>();
      superTypes.add(a);
      type.setSuperTypes(superTypes);
    }
    sym.setType(type);
    list.add(sym);
    return list;
  }

}
