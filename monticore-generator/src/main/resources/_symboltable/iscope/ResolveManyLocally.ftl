<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleName", "symbolFullName")}
  final List<${symbolFullName}> resolvedSymbols = new ArrayList<>();

  try {
    Optional<${symbolFullName}> resolvedSymbol = filter${simpleName}(name, get${simpleName}Symbols());
    if (resolvedSymbol.isPresent()) {
      resolvedSymbols.add(resolvedSymbol.get());
    }
  } catch (de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException e) {
    resolvedSymbols.addAll(e.getSymbols());
  }

  // add all symbols of sub kinds of the current kind
  resolvedSymbols.addAll(resolve${simpleName}SubKinds(foundSymbols, name, modifier, predicate));

  // filter out symbols that are not included within the access modifier
  List<${symbolFullName}> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
  filteredSymbols = new ArrayList<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toList()));

  //try to find adapted one
  filteredSymbols.addAll(resolveAdapted${simpleName}LocallyMany(foundSymbols, name, modifier, predicate));
  filteredSymbols = filterSymbolsByAccessModifier(modifier, filteredSymbols);
  filteredSymbols = new de.monticore.symboltable.SetAsListAdapter<>(filteredSymbols.stream().filter(predicate).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));

  return filteredSymbols;