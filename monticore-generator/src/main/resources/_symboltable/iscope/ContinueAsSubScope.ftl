<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
  set${simpleName}SymbolsAlreadyResolved(false);
  if (checkIfContinueAsSubScope(name)) {
    final String remainingSymbolName = getRemainingNameForResolveDown(name);
    return this.resolve${simpleName}DownMany(foundSymbols, remainingSymbolName, modifier, predicate);
  }
  return new ArrayList<>();