// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public interface IBasicSymbolsScope extends IBasicSymbolsScopeTOP {

  /**
   * returns whether a type variable is bound within this scope
   * e.g. {@code class C<T> {}} // T is bound within the class
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
   * This method intentionally resolves only symbols locally in the current scope and does not
   * consider symbols from supertypes of the enclosing type or scope. For resolution including
   * supertypes, use {@link de.monticore.types3.util.WithinScopeBasicSymbolsResolver} instead.
   */
  @Override
  default List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                          Predicate<FunctionSymbol> predicate) {
    return IBasicSymbolsScopeTOP.super.resolveFunctionLocallyMany(foundSymbols,name,modifier,predicate);
  }

  /**
   * This method intentionally resolves only symbols locally in the current scope and does not
   * consider symbols from supertypes of the enclosing type or scope. For resolution including
   * supertypes, use {@link de.monticore.types3.util.WithinScopeBasicSymbolsResolver} instead.
   */
  @Override
  default List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate){
    return IBasicSymbolsScopeTOP.super.resolveVariableLocallyMany(foundSymbols,name,modifier,predicate);
  }


}
