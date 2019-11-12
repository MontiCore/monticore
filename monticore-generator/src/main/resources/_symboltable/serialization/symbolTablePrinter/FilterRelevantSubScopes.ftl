<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface")}
  List<${scopeInterface}> result = new ArrayList<>();
  for (${scopeInterface} scope : subScopes) {
    if(hasSymbolsInSubScopes(scope)) {
      result.add(scope);
    }
  }
  return result;