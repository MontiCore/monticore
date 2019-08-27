<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName")}
  if (!is${simpleName}SymbolsAlreadyResolved()) {
    set${simpleName}SymbolsAlreadyResolved(true);
  } else {
    return new LinkedHashSet<>();
  }

  final Set<${symbolFullName}> resolvedSymbols = this.resolve${simpleName}LocallyMany(foundSymbols, name, modifier, predicate);
  if (!resolvedSymbols.isEmpty()) {
    set${simpleName}SymbolsAlreadyResolved(false);
    return resolvedSymbols;
  }
  resolvedSymbols.addAll(resolveAdapted${simpleName}LocallyMany(foundSymbols, name, modifier, predicate));
  if (!resolvedSymbols.isEmpty()) {
    set${simpleName}SymbolsAlreadyResolved(false);
    return resolvedSymbols;
  }
  final Collection<${symbolFullName}> resolvedFromEnclosing = continue${simpleName}WithEnclosingScope((foundSymbols | resolvedSymbols.size() > 0), name, modifier, predicate);
  resolvedSymbols.addAll(resolvedFromEnclosing);
  set${simpleName}SymbolsAlreadyResolved(false);
  return resolvedSymbols;