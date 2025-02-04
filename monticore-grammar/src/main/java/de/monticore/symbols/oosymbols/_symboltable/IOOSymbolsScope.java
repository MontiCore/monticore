/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public  interface IOOSymbolsScope extends IOOSymbolsScopeTOP  {

  /**
   * override method from ExpressionBasisScope to resolve all methods correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting methods in its super types too because of inheritance
   * the method resolves the methods like the overridden method and if the spanning symbol is a type symbol it additionally looks for methods in its super types
   * it is used by the method getMethodList in SymTypeExpression
   */
  @Override
  default List<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                         Predicate<MethodSymbol> predicate) {
    //resolve methods by using overridden method
    List<MethodSymbol> set = IOOSymbolsScopeTOP.super.resolveMethodLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the methodsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof OOTypeSymbol){
        OOTypeSymbol typeSymbol = (OOTypeSymbol) spanningSymbol;
        for(SymTypeExpression t : typeSymbol.getSuperTypesList()){
          t.getMethodList(name, false, modifier).stream().
                  filter(m -> m instanceof MethodSymbol).forEach(m -> set.add((MethodSymbol) m));
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
  default List<FieldSymbol> resolveFieldLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FieldSymbol> predicate){
    //resolve methods by using overridden method
    List<FieldSymbol> result = IOOSymbolsScopeTOP.super.resolveFieldLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol() && modifier.includes(StaticAccessModifier.NON_STATIC)){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the fieldsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof OOTypeSymbol){
        OOTypeSymbol typeSymbol = (OOTypeSymbol) spanningSymbol;
        for(SymTypeExpression superType : typeSymbol.getSuperTypesList()){
         superType.getFieldList(name, false, modifier).stream().
                 filter(f -> f instanceof FieldSymbol).forEach(f -> result.add((FieldSymbol) f));
        }
      }
    }
    return result;
  }

  /**
   * override method from ExpressionBasisScope to resolve all fields correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting fields in its super types too because of inheritance
   * the method resolves the fields like the overridden method and if the spanning symbol is a type symbol it additionally looks for fields in its super types
   * it is used by the method getFieldList in SymTypeExpression
   */
  @Override
  default List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate){
    final List<de.monticore.symbols.basicsymbols._symboltable.VariableSymbol> resolvedSymbols = new ArrayList<>();

    try {
      Optional<VariableSymbol> resolvedSymbol = filterVariable(name, getVariableSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    } catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // add all symbols of sub kinds of the current kind
    resolvedSymbols.addAll(resolveVariableSubKinds(foundSymbols, name, modifier, predicate));

    // filter out symbols that are not included within the access modifier
    List<de.monticore.symbols.basicsymbols._symboltable.VariableSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new ArrayList<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList()));

    //try to find adapted one
    filteredSymbols.addAll(resolveAdaptedVariableLocallyMany(foundSymbols, name, modifier, predicate));
    filteredSymbols = filterSymbolsByAccessModifier(modifier, filteredSymbols);
    filteredSymbols = new ArrayList<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList()));

    return filteredSymbols;
  }

  @Override
  default List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FunctionSymbol> predicate) {
    final List<de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol> resolvedSymbols = new ArrayList<>();

    try {
      Optional<de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol> resolvedSymbol = filterFunction(name, getFunctionSymbols());
      if (resolvedSymbol.isPresent()) {
        resolvedSymbols.add(resolvedSymbol.get());
      }
    } catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e) {
      resolvedSymbols.addAll(e.getSymbols());
    }

    // add all symbols of sub kinds of the current kind
    resolvedSymbols.addAll(resolveFunctionSubKinds(foundSymbols, name, modifier, predicate));

    // filter out symbols that are not included within the access modifier
    List<de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new ArrayList<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList()));

    //try to find adapted one
    filteredSymbols.addAll(resolveAdaptedFunctionLocallyMany(foundSymbols, name, modifier, predicate));
    filteredSymbols = filterSymbolsByAccessModifier(modifier, filteredSymbols);
    filteredSymbols = new ArrayList<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList()));

    return filteredSymbols;
  }
}
