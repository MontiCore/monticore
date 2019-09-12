<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName", "scopeInterface")}
  if (!is${simpleName}SymbolsAlreadyResolved()) {
    set${simpleName}SymbolsAlreadyResolved(true);
  } else {
    return new LinkedHashSet<>();
  }

  // 1. Conduct search locally in the current scope
  final Set<${symbolFullName}> resolved = this.resolve${simpleName}LocallyMany(foundSymbols, name, modifier, predicate);

  foundSymbols = foundSymbols | resolved.size() > 0;

  final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "${simpleName}Symbol"
    + "\") in scope \"" + getNameOpt() + "\"";
  Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
  // If no matching symbols have been found...
  if (resolved.isEmpty()) {
    // 2. Continue search in sub scopes and ...
    for (${scopeInterface} subScope : getSubScopes()) {
      final Collection<${symbolFullName}> resolvedFromSub = subScope
        .continueAs${simpleName}SubScope(foundSymbols, name, modifier, predicate);
      foundSymbols = foundSymbols | resolved.size() > 0;
      // 3. unify results
      resolved.addAll(resolvedFromSub);
    }
  }
  Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");
  set${simpleName}SymbolsAlreadyResolved(false);
  return resolved;