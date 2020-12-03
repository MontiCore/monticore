<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName")}
  List<${simpleName}Symbol> resultList = new ArrayList<>();
  set${simpleName}SymbolsAlreadyResolved(false);
  if (checkIfContinueAsSubScope(name)) {
    for(String remainingSymbolName: getRemainingNameForResolveDown(name)) {
      resultList.addAll(this.resolve${simpleName}DownMany(foundSymbols, remainingSymbolName, modifier, predicate));
    }
  }
  return resultList;