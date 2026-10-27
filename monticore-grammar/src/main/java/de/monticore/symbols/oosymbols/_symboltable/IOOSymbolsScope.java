/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public  interface IOOSymbolsScope extends IOOSymbolsScopeTOP  {
  // Class2MC incorrectly references IOOSymbolsScopeTOP, which
  // is not generated, when we remove this class
  // https://bugs.openjdk.org/browse/JDK-8301046

  /**
   * This method intentionally resolves only symbols locally in the current scope and does not
   * consider symbols from supertypes of the enclosing type or scope. For resolution including
   * supertypes, use {@link de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver} instead.
   */
  @Override
  default List<FieldSymbol> resolveFieldLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FieldSymbol> predicate){
    return IOOSymbolsScopeTOP.super.resolveFieldLocallyMany(foundSymbols,name,modifier,predicate);
  }

  /**
   * This method intentionally resolves only symbols locally in the current scope and does not
   * consider symbols from supertypes of the enclosing type or scope. For resolution including
   * supertypes, use {@link de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver} instead.
   */
  @Override
  default List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate){
    return IOOSymbolsScopeTOP.super.resolveVariableLocallyMany(foundSymbols,name,modifier,predicate);
  }

  /**
   * This method intentionally resolves only symbols locally in the current scope and does not
   * consider symbols from supertypes of the enclosing type or scope. For resolution including
   * supertypes, use {@link de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver} instead.
   */
  @Override
  default List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FunctionSymbol> predicate) {
    return IOOSymbolsScopeTOP.super.resolveFunctionLocallyMany(foundSymbols,name,modifier,predicate);
  }
}
