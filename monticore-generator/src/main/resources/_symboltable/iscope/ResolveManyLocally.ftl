<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName")}
  final Set<${symbolFullName}> resolvedSymbols = new LinkedHashSet<>();

  try {
  // TODO remove filter?
    Optional<${symbolFullName}> resolvedSymbol = filter${simpleName}(name, get${simpleName}Symbols());
    if (resolvedSymbol.isPresent()) {
      resolvedSymbols.add(resolvedSymbol.get());
    }
  } catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e) {
    resolvedSymbols.addAll(e.getSymbols());
  }

      // filter out symbols that are not included within the access modifier
  Set<${symbolFullName}> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
  filteredSymbols = new LinkedHashSet<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toSet()));

  return filteredSymbols;