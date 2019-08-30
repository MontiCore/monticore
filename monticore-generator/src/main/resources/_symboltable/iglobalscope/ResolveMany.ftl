<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName")}
  // First, try to resolve the symbol in the current scope and its sub scopes.
  Collection<${symbolFullName}> resolvedSymbol = resolve${simpleName}DownMany(foundSymbols, name,  modifier, predicate);

  if (!resolvedSymbol.isEmpty()) {
    return resolvedSymbol;
  }

  // Symbol not found: try to load corresponding model and build its symbol table
  loadModelsFor${simpleName}(name);

  // Maybe the symbol now exists in this scope (or its sub scopes). So, resolve down, again.
  resolvedSymbol = resolve${simpleName}DownMany(false, name, modifier, predicate);
  foundSymbols = foundSymbols  | resolvedSymbol.size() > 0;
  if (!foundSymbols && !is${simpleName}SymbolsAlreadyResolved()){
    set${simpleName}SymbolsAlreadyResolved(true);
    resolvedSymbol.addAll(resolveAdapted${simpleName}(foundSymbols, name, modifier, predicate));
  }
  set${simpleName}SymbolsAlreadyResolved(false);
  return resolvedSymbol;