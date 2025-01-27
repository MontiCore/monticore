// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.DeprecatedSymTypeExpressionSymbolResolver;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;
import java.util.function.Predicate;

public interface IBasicSymbolsScope extends IBasicSymbolsScopeTOP {

  /**
   * returns whether a type variable is bound within this scope
   * e.g. class C<T> {} // T is bound within the class
   */
  default boolean isTypeVariableBound(TypeVarSymbol typeVar) {
    List<TypeVarSymbol> localVars = resolveTypeVarLocallyMany(
        false, typeVar.getName(),
        AccessModifier.ALL_INCLUSION, (tv) -> true
    );
    if (localVars.stream()
        .anyMatch(otherTypeVar -> otherTypeVar == typeVar)) {
      return true;
    }
    else if (getEnclosingScope() == null) {
      return false;
    }
    else {
      return getEnclosingScope().isTypeVariableBound(typeVar);
    }
  }

  /**
   * override method from ExpressionBasisScope to resolve all methods correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting methods in its super types too because of inheritance
   * the method resolves the methods like the overridden method and if the spanning symbol is a type symbol it additionally looks for methods in its super types
   * it is used by the method getMethodList in SymTypeExpression
   */
  @Override
  default List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                          Predicate<FunctionSymbol> predicate) {
    //resolve methods by using overridden method
    List<FunctionSymbol> set = IBasicSymbolsScopeTOP.super.resolveFunctionLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the methodsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = ((TypeSymbol) spanningSymbol);
        for(SymTypeExpression t : typeSymbol.getSuperTypesList()){
          set.addAll(DeprecatedSymTypeExpressionSymbolResolver.getMethodList(t, name, true, modifier));
        }
      }
    }
    return set;
  }

  /**
   * override method from ExpressionBasisScope to resolve all fields correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting fields in its super types too because of inheritance
   * the method resolves the fields like the overridden method and if the spanning symbol is a type symbol it additionally looks for fields in its super types
   * it is used by the method getFieldList in SymTypeExpression
   */
  @Override
  default List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate){
    //resolve methods by using overridden method
    List<VariableSymbol> result = IBasicSymbolsScopeTOP.super.resolveVariableLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the fieldsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression superType : typeSymbol.getSuperTypesList()){
          result.addAll(DeprecatedSymTypeExpressionSymbolResolver.getFieldList(superType, name, true, modifier));
        }
      }
    }
    return result;
  }


}
