<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "symbolFullName")}
  final Set<${symbolFullName}> resolvedSymbols = new LinkedHashSet<>();

  final String simpleName = de.se_rwth.commons.Names.getSimpleName(name);

  if (symbols.containsKey(simpleName)) {
    for (${symbolFullName} symbol : symbols.get(simpleName)) {
      if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
        resolvedSymbols.add(symbol);
      }
    }
  }

  return getResolvedOrThrowException(resolvedSymbols);