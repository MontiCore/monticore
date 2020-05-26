<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName", "scopeInterface")}
  // skip resolution of the symbol, if the symbol has already been resolved in this scope instance
  // during the current execution of the resolution algorithm
  if (is${simpleName}SymbolsAlreadyResolved()) {
    return new ArrayList<>();
  }

  // (1) resolve symbol locally. During this, the 'already resolved' flag is set to true,
  // to prevent resolving cycles caused by cyclic symbol adapters
  set${simpleName}SymbolsAlreadyResolved(true);
  final List<${symbolFullName}> resolvedSymbols = this.resolve${simpleName}LocallyMany(foundSymbols, name, modifier, predicate);
  foundSymbols = foundSymbols | resolvedSymbols.size() > 0;
  set${simpleName}SymbolsAlreadyResolved(false);

  final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "${simpleName}Symbol"
    + "\") in scope \"" + (isPresentName() ? getName() : "") + "\"";
  Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");
  // If no matching symbols have been found...
  if (resolvedSymbols.isEmpty()) {
    // (2) Continue search in sub scopes and ...
    for (${scopeInterface} subScope : getSubScopes()) {
      final List<${symbolFullName}> resolvedFromSub = subScope
        .continueAs${simpleName}SubScope(foundSymbols, name, modifier, predicate);
      foundSymbols = foundSymbols | resolvedFromSub.size() > 0;
      // (3) unify results
      resolvedSymbols.addAll(resolvedFromSub);
    }
  }
  Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
  set${simpleName}SymbolsAlreadyResolved(false);
  return resolvedSymbols;